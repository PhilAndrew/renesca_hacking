package renesca.json.protocols

import org.json4s.native.Serialization
import renesca.Query
import renesca.json._
import org.json4s._
import org.json4s.JsonDSL._
import native.JsonMethods._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization.{read, write}
import org.json4s._
import native.JsonMethods._

object ResponseJson {

  // Need ... Relationship Node Graph Error Data Result Transaction Response

  implicit val formats = Serialization.formats(NoTypeHints) + new PropertyKeySerializer() + new ParameterValueSerializer() + new PropertyValueSerializer()

  def jsonToObject(jsonResponse: String): Response = {
    val p = parse(jsonResponse)
    p.extract[Response]
  }

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