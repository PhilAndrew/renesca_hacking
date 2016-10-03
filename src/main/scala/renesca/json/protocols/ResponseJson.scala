package renesca.json.protocols

import org.json4s.native.Serialization
import renesca.Query
import renesca.json._
import renesca.json.protocols.ExampleJson.Child
import org.json4s._
import org.json4s.JsonDSL._
import native.JsonMethods._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization.{read, write}

object ResponseJson {

  // Need ... Relationship Node Graph Error Data Result Transaction Response

  implicit val formats = Serialization.formats(NoTypeHints)

  def jsonOf(obj: Relationship): String = {
    write(obj)
  }

  def jsonOf(obj: Node): String = {
    write(obj)
  }

  def jsonOf(obj: Graph): String = {
    write(obj)
  }

  def jsonOf(obj: Data): String = {
    write(obj)
  }

  def jsonOf(obj: Result): String = {
    write(obj)
  }

  def jsonOf(obj: Transaction): String = {
    write(obj)
  }

  def jsonOf(obj: Response): String = {
    write(obj)
  }

}
/*
import renesca.json._
import spray.json.DefaultJsonProtocol

object ResponseJsonProtocol extends DefaultJsonProtocol {

  import renesca.json.protocols.ParameterProtocol._

  implicit val relationshipFormat = jsonFormat5(Relationship)
  implicit val nodeFormat = jsonFormat3(Node)
  implicit val graphDataFormat = jsonFormat2(Graph)
  implicit val errorFormat = jsonFormat2(Error)
  implicit val dataFormat = jsonFormat2(Data)
  implicit val resultFormat = jsonFormat2(Result)
  implicit val transactionFormat = jsonFormat1(Transaction)
  implicit val responseFormat = jsonFormat4(Response)
}
*/