package renesca.json.protocols

import org.json4s.native.Serialization
import renesca.Query
import renesca.json.{Request, Statement}
import renesca.json.protocols.ExampleJson.Child

import org.json4s._
import org.json4s.JsonDSL._
import native.JsonMethods._

import org.json4s.native.Serialization._
import org.json4s.native.Serialization.{read, write}

object RequestJson {

  implicit val formats = Serialization.formats(NoTypeHints)

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



/*
import renesca.json.protocols.ParameterProtocol._
import renesca.json.{Request, Statement}
import spray.json._

object RequestJsonProtocol extends DefaultJsonProtocol {
  implicit val statementFormat = jsonFormat3(Statement.apply)
  implicit val requestFormat = jsonFormat1(Request)
}

*/