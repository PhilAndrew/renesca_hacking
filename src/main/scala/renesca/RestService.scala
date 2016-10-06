package renesca

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, Location, RawHeader}
import akka.util.{ByteString, Timeout}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import renesca.json.protocols.{RequestJson, ResponseJson}

/**
  * Notes:
  *
  * This code was changed convert it from Spray to Akka HTTP, it is mostly consistent with the previous version
  * except for the error handling which is not consistent as I am not sure how to make the same error handling using
  * the Akka http API code as it used to use the Spray code.
  *
  * Blocking is evil.
  * Await.result should be changed so that we return a Future[_] rather than blocking to wait for a response then
  * in code calling this we should deal with Future[_]'s.
  *
  * actorSystem: ActorSystem and materializer should be implicit parameters as we may not want to have a completely
  * independent ActorSystem here, we may want to use the globally used ActorSystem from within the program.
  */

case class TransactionId(id: String) {
  override def toString = id
}

class RestService(val server: String, credentials: Option[BasicHttpCredentials] = None, implicit val timeout: Timeout = Timeout(60.seconds)) {
  implicit val actorSystem: ActorSystem = ActorSystem()
  import actorSystem.dispatcher

  implicit val materializer = ActorMaterializer()
  val http = Http()

  def pipeline(r: HttpRequest) = http.singleRequest(r)

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

    val json = RequestJson.jsonOf(jsonRequest)

    val i = scala.util.Random.nextInt()
    println("")
    println(i.toString + " " + "JSON request ")
    println(i.toString + " " + json)

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = buildUri(path),
      headers = headers.toList,
      entity = HttpEntity.Strict(MediaTypes.`application/json`, ByteString(json))
    )
    pipeline(request)
  }

  def parseJson(jsonResponse: String): json.Response = {
    ResponseJson.jsonToObject(jsonResponse)
  }

  private def awaitResponse(path: String, jsonRequest: json.Request): Future[(List[HttpHeader], json.Response)] = {
    val httpResponse = buildHttpPostRequest(path, jsonRequest)

    val responseFuture: Future[(Seq[HttpHeader],json.Response)] = for (response <- httpResponse;
                              jsonResponse <- Unmarshal(response.entity).to[String]) yield (response.headers, {

      val i = scala.util.Random.nextInt()
      println("")
      println(i.toString + " " + "JSON response ")
      println(i.toString + " " + jsonResponse)

      parseJson(jsonResponse)
    })

    // @todo Note the error handling is not consistent with the previous Spray code
    // case Left(deserializationError) => throw new RuntimeException(s"Deserialization Error: $deserializationError\n\n${ httpResponse.entity.asString }")

    responseFuture.map( (r) => (r._1.toList, r._2))
  }

  def singleRequest(jsonRequest: json.Request): Future[json.Response] = {
    val path = "/db/data/transaction/commit"
    awaitResponse(path, jsonRequest).map( (r) => r._2)
  }

  def openTransaction(jsonRequest: json.Request = json.Request()): Future[(TransactionId, json.Response)] = {
    val path = "/db/data/transaction"

    awaitResponse(path, jsonRequest).map( (r) => {
      val headers = r._1
      val jsonResponse = r._2

      val uris = headers.collectFirst({ case Location(uri) => uri })
      val optionId = for(uri <- uris) yield {
        uri.path.reverse.head.toString
      }

      optionId match {
        case Some(id) => (TransactionId(id), jsonResponse)
        case None     => throw new RuntimeException("Cannot get transaction id")
      }
    })
  }

  def resumeTransaction(id: TransactionId, jsonRequest: json.Request): Future[json.Response] = {
    val path = s"/db/data/transaction/$id"
    awaitResponse(path, jsonRequest).map( (r) => r._2)
  }

  def commitTransaction(id: TransactionId, jsonRequest: json.Request = json.Request()): Future[json.Response] = {
    val path = s"/db/data/transaction/$id/commit"
    awaitResponse(path, jsonRequest).map( (r) => r._2)
  }

  def rollbackTransaction(id: TransactionId): Future[Unit] = {
    // we don't wait for a response here
    val path = s"/db/data/transaction/$id"
    pipeline(HttpRequest(HttpMethods.DELETE, buildUri(path))).map(f => Unit)
  }

  override def toString = s"RestService($server${ if(credentials.isDefined) " with credentials" else "" }, $timeout)"
}

