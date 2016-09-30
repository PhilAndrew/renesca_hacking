package renesca

import akka.http.scaladsl.marshalling.{Marshal, Marshalling, ToResponseMarshallable}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, Location, RawHeader}
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import SprayJsonSupport._

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

  private def buildHttpPostRequest(path: String, jsonRequest: json.Request): Future[HttpResponse] = {
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

    val m = Marshal(jsonRequest)
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = buildUri(path),
      headers = headers.toList
      //entity = HttpEntity(MediaTypes.`application/json`, "")
    )
    val z: Future[HttpResponse] = m.toResponseFor(request)
    val z2: HttpResponse = Await.result(z, RestService.timeoutMilliseconds)
    val json: String = Await.result(z2.entity.toStrict(RestService.timeoutMilliseconds).map( (e: HttpEntity.Strict) => {
      e.data.decodeString("US-ASCII")
    }), 10000 milliseconds)
    val sendRequest = request.copy(entity = HttpEntity(MediaTypes.`application/json`, json))
   pipeline(sendRequest)
  }

  private def awaitResponse(path: String, jsonRequest: json.Request): (List[HttpHeader], json.Response) = {
    val httpResponse = buildHttpPostRequest(path, jsonRequest)
    import renesca.json.protocols.ResponseJsonProtocol._
    val httpResponse2: HttpResponse = Await.result(httpResponse, RestService.timeoutMilliseconds)  //awaitResponse(httpRequest)
    val s = Await.result(Unmarshal(httpResponse2.entity).to[String], RestService.timeoutMilliseconds)
    val responseFuture: Future[json.Response] = Unmarshal(httpResponse2.entity).to[json.Response] //.toStrict(30000 milliseconds).map((f: HttpEntity.Strict) => f.data)
    val jsonResponse: json.Response = Await.result(responseFuture, RestService.timeoutMilliseconds)
    //TODO: error handling
    (httpResponse2.headers.toList, jsonResponse)
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

