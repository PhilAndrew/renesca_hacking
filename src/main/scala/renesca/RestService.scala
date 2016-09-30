package renesca

import akka.http.scaladsl.marshalling.{Marshal, Marshaller, Marshalling, ToResponseMarshallable}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, Location, RawHeader}
import akka.util.{ByteString, Timeout}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import SprayJsonSupport._
import akka.stream.scaladsl.Source

case class TransactionId(id: String) {
  override def toString = id
}

object RestService {
  val timeoutMilliseconds = 30000 milliseconds
}

class RestService(val server: String, credentials: Option[BasicHttpCredentials] = None, implicit val timeout: Timeout = Timeout(60.seconds)) {
  // http://spray.io/documentation/1.2.2/spray-can/http-client/request-level/
  // http://spray.io/documentation/1.2.2/spray-client/
  implicit val actorSystem: ActorSystem = ActorSystem()

  // dispatcher provides execution context
  import actorSystem.dispatcher

  //val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  private def awaitResponse(request: HttpRequest): HttpResponse = Await.result(pipeline(request), timeout.duration)

  private def buildUri(path: String) = Uri(s"$server$path")

  private def buildHttpPostRequest(path: String, jsonRequest: renesca.json.Request): Future[HttpResponse] = {
    val headers = new mutable.ListBuffer[HttpHeader]()
    //TODO: Accept: application/json; charset=UTF-8 - is this necessary?
    // val accept:MediaRange = `application/json`// withCharset `UTF-8`
    // headers += Accept(accept)
    headers ++= credentials.map(Authorization(_))
    // accept streaming of results from rest endpoint
    // http://neo4j.com/docs/2.2.3/rest-api-streaming.html
    headers += RawHeader("X-Stream", "true")

    import SprayJsonSupport._
    import renesca.json.protocols.RequestJsonProtocol._

    Marshal(jsonRequest).to[RequestEntity].map( (e) => {
      HttpRequest(
        method = HttpMethods.POST,
        uri = buildUri(path),
        headers = headers.toList,
        entity = e
      )
    }).flatMap(pipeline)
  }

  private def awaitResponse(path: String, jsonRequest: json.Request): (List[HttpHeader], json.Response) = {
    val httpResponse = buildHttpPostRequest(path, jsonRequest)
    import renesca.json.protocols.ResponseJsonProtocol._

    val responseFuture = for (response <- httpResponse;
         stringResponse <- Unmarshal(response.entity).to[String];
         jsonResponse <- Unmarshal(stringResponse).to[json.Response]) yield (response.headers, jsonResponse)

    val response = Await.result(responseFuture, RestService.timeoutMilliseconds)
    //TODO: error handling

    (response._1.toList, response._2)
  }

  def singleRequest(jsonRequest: json.Request): json.Response = {
    val path = "/db/data/transaction/commit"
    val (_, jsonResponse) = awaitResponse(path, jsonRequest)
    jsonResponse
  }

  def openTransaction(jsonRequest: json.Request = json.Request()): (TransactionId, json.Response) = {
    val path = "/db/data/transaction"
    val (headers, jsonResponse) = awaitResponse(path, jsonRequest)
    val uris = headers.collectFirst({ case Location(uri) => uri })
    val optionId = for(uri <- uris) yield {
      uri.path.reverse.head.toString
    }

    optionId match {
      case Some(id) => (TransactionId(id), jsonResponse)
      case None     => throw new RuntimeException("Cannot get transaction id")
    }
  }

  def resumeTransaction(id: TransactionId, jsonRequest: json.Request): json.Response = {
    val path = s"/db/data/transaction/$id"
    val (_, jsonResponse) = awaitResponse(path, jsonRequest)
    jsonResponse
  }

  def commitTransaction(id: TransactionId, jsonRequest: json.Request = json.Request()): json.Response = {
    val path = s"/db/data/transaction/$id/commit"
    val (_, jsonResponse) = awaitResponse(path, jsonRequest)
    jsonResponse
  }

  implicit val materializer = ActorMaterializer()  // @todo Added by Philip, should use this?
  val http = Http()

  def pipeline(r: HttpRequest) = http.singleRequest(r)

  def rollbackTransaction(id: TransactionId) {
    // we don't wait for a response here
    val path = s"/db/data/transaction/$id"
    pipeline(HttpRequest(HttpMethods.DELETE, buildUri(path)))
  }

  override def toString = s"RestService($server${ if(credentials.isDefined) " with credentials" else "" }, $timeout)"
}

