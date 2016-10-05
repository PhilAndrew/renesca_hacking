package renesca.json.protocols

import org.json4s.native.Serialization
import renesca.Query
import renesca.json.{Request, Statement}
import org.json4s._
import org.json4s.JsonDSL._
import native.JsonMethods._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization.{read, write}
import renesca.parameter._


object RequestJson {

  implicit val formats = Serialization.formats(NoTypeHints) + new PropertyKeySerializer() + new ParameterMapSerializer()

  def jsonOf(obj: Request): String = {
    write(obj)
  }

  def jsonOf(obj: Statement): String = {
    write(obj)
  }

  def jsonOfStatement(query: Query, resultDataContents: List[String]): String = {
    val a = Statement.apply(query, resultDataContents)
    jsonOf(a)
  }

}