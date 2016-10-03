package renesca.json.protocols

import org.json4s.native.Serialization
import renesca.json.{Request, Statement}

object ExampleJson {

  def test() = {
    val request = Request(List(Statement(statement = "", parameters = None, resultDataContents = Some(List("what")))))
    val json = RequestJson.jsonOf(request)
    println(json)

  }








  // statement: String, parameters: Option[ParameterMap] = None, resultDataContents: Option[List[String]] = None

  import org.json4s._
  import org.json4s.JsonDSL._
  import native.JsonMethods._



  case class SetWrapper(set: Set[String])

  case class Person(name: String, address: Address, children: List[Child])
  case class Address(street: String, city: String)
  case class Child(name: String, age: Int, birthdate: Option[java.util.Date])

  case class SimplePerson(name: String, address: Address)

  case class PersonWithMap(name: String, address: Map[String, String])
  case class PersonWithAddresses(name: String, addresses: Map[String, Address])

  case class Name(name: String)

  case class Primitives(i: Int, l: Long, d: Double, f: Float, s: String, sym: Symbol, sh: Short, b: Byte, bool: Boolean)

  case class OChild(name: Option[String], age: Int, mother: Option[Parent], father: Option[Parent])
  case class Parent(name: String)

  import java.util.Date
  case class Event(name: String, timestamp: Date)

  case class MultiDim(ints: List[List[List[Int]]], names: List[List[Name]])

  case class MultipleConstructors(name: String, age: Int, size: Option[String]) {
    def this(name: String) = this(name, 30, None)

    def this(age: Int, name: String) = this(name, age, Some("S"))

    def this(name: String, birthYear: Int) = this(name, 2010 - birthYear, None)

    def this(size: Option[String], age: Int) = this("unknown", age, size)
  }

  case class ClassWithJSON(name: String, message: JValue)

  sealed trait LeafTree[+T]
  object LeafTree {
    def empty[T]: LeafTree[T] = EmptyLeaf
  }

  case class Node[T](children: List[LeafTree[T]]) extends LeafTree[T]
  case class Leaf[T](value: T) extends LeafTree[T]
  case object EmptyLeaf extends LeafTree[Nothing]

  case class WithDefaultValueHolder(values: Seq[WithDefaultValue])
  case class WithDefaultValue(name: String, gender: String = "male")







  case class Quiz(id: String, question: String, correctAnswer: String)

  case object QuizCreated

  case object QuizAlreadyExists

  case object QuizDeleted

  case class Question(id: String, question: String)

  case object QuestionNotFound

  case class Answer(answer: String)

  case object CorrectAnswer

  case object WrongAnswer

  implicit def toQuestion(quiz: Quiz): Question = Question(id = quiz.id, question = quiz.question)

  implicit def toAnswer(quiz: Quiz): Answer = Answer(answer = quiz.correctAnswer)






  def classClassToJson() = {
    import org.json4s.native.Serialization._
    import org.json4s.native.Serialization.{read, write}

    implicit val formats = Serialization.formats(NoTypeHints)
    val ser = write(Child("Mary", 5, None))
    println(ser)
  }

  def jsonStringToCaseClasses() = {
    implicit val formats = DefaultFormats

    case class Winner(id: Long, numbers: List[Int])
    case class Lotto(id: Long, winningNumbers: List[Int], winners: List[Winner], drawDate: Option[java.util.Date])

    val winners = List(Winner(23, List(2, 45, 34, 23, 3, 5)), Winner(54, List(52, 3, 12, 11, 18, 22)))
    val lotto = Lotto(5, List(2, 45, 34, 23, 7, 5, 3), winners, None)

    val json = parse(
      """
           { "name": "joe",
             "address": {
               "street": "Bulevard",
               "city": "Helsinki"
             },
             "children": [
               {
                 "name": "Mary",
                 "age": 5,
                 "birthdate": "2004-09-04T18:06:22Z"
               },
               {
                 "name": "Mazy",
                 "age": 3
               }
             ]
           }
         """)


    json.extract[Person]


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