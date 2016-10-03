package renesca.json.protocols

import org.json4s.native.Serialization
import renesca.json.{Request, Statement}

object ExampleJson {

  def test() = {
    val request = Request(List(Statement(statement = "", parameters = None, resultDataContents = Some(List("graph")))))
    val json = RequestJson.jsonOf(request)
    println(json)
// {"statements":[{"statement":"","resultDataContents":["what"]}]}
  }


/*
Multiple main classes detected, select one to run:

 [1] universe.TestRenescaApp
 [2] universe.TestRenescaApp2

Enter number: 1

[info] Running universe.TestRenescaApp


{"statements":[{"statement":"match (n) optional match (n)-[r]-() return n,r","re
sultDataContents":["graph"]}]}

FulfilledFuture({"results":[{"columns":["n","r"],"data":[]}],"errors":[]})

### extractGraphs
List(Result(List(n, r),List()))


{"statements":[{"statement":"CREATE (:ANIMAL {name:'snake'})-[:EATS]->(:ANIMAL {
name:'dog'})"}]}

FulfilledFuture({"results":[{"columns":[],"data":[]}],"errors":[]})



{"statements":[{"statement":"MATCH (n:ANIMAL)-[r]->() RETURN n,r","resultDataCon
tents":["graph"]}]}

FulfilledFuture({"commit":"http://192.168.9.131:7474/db/data/transaction/306/com
mit","results":[{"columns":["n","r"],"data":[{"graph":{"nodes":[{"id":"116","lab
els":["ANIMAL"],"properties":{"name":"snake"}},{"id":"117","labels":["ANIMAL"],"
properties":{"name":"dog"}}],"relationships":[{"id":"89","type":"EATS","startNod
e":"116","endNode":"117","properties":{}}]}}]}],"transaction":{"expires":"Mon, 0
3 Oct 2016 13:13:30 +0000"},"errors":[]})

### extractGraphs
List(Result(List(n, r),List(Data(None,Some(Graph(List(Node(116,List(ANIMAL),Map(
name -> snake)), Node(117,List(ANIMAL),Map(name -> dog))),List(Relationship(89,E
ATS,116,117,Map()))))))))
Name of one snake neighbour: dog


{"statements":[{"statement":"match (V0) where id(V0) = {V0_itemId} set V0 += {V0
_propertyAdditions}  set V0:`REPTILE` ","parameters":{"V0_itemId":116,"V0_proper
tyAdditions":{"hungry":true}},"resultDataContents":["row","graph"]},{"statement"
:"create (V0 :`ANIMAL` {V0_properties})  return V0","parameters":{"V0_properties
":{"name":"hippo"}},"resultDataContents":["row","graph"]}]}

FulfilledFuture({"commit":"http://192.168.9.131:7474/db/data/transaction/306/com
mit","results":[{"columns":[],"data":[]},{"columns":["V0"],"data":[{"row":[{"nam
e":"hippo"}],"meta":[{"id":118,"type":"node","deleted":false}],"graph":{"nodes":
[{"id":"118","labels":["ANIMAL"],"properties":{"name":"hippo"}}],"relationships"
:[]}}]}],"transaction":{"expires":"Mon, 03 Oct 2016 13:13:30 +0000"},"errors":[]
})

### extractGraphs
List(Result(List(),List()), Result(List(V0),List(Data(Some(ArraySeq(Map(name ->
hippo))),Some(Graph(List(Node(118,List(ANIMAL),Map(name -> hippo))),List()))))))



{"statements":[{"statement":"match (V0) where id(V0) = {V0_nodeId} match (V1) wh
ere id(V1) = {V1_nodeId} create (V0)-[V2 :`EATS` {V2_properties}]->(V1)  return
V2","parameters":{"V0_nodeId":116,"V2_properties":{},"V1_nodeId":118},"resultDat
aContents":["row","graph"]}]}

FulfilledFuture({"commit":"http://192.168.9.131:7474/db/data/transaction/306/com
mit","results":[{"columns":["V2"],"data":[{"row":[{}],"meta":[{"id":90,"type":"r
elationship","deleted":false}],"graph":{"nodes":[{"id":"116","labels":["ANIMAL",
"REPTILE"],"properties":{"hungry":true,"name":"snake"}},{"id":"118","labels":["A
NIMAL"],"properties":{"name":"hippo"}}],"relationships":[{"id":"90","type":"EATS
","startNode":"116","endNode":"118","properties":{}}]}}]}],"transaction":{"expir
es":"Mon, 03 Oct 2016 13:13:30 +0000"},"errors":[]})

### extractGraphs
List(Result(List(V2),List(Data(Some(ArraySeq(Map())),Some(Graph(List(Node(116,Li
st(ANIMAL, REPTILE),Map(hungry -> true, name -> snake)), Node(118,List(ANIMAL),M
ap(name -> hippo))),List(Relationship(90,EATS,116,118,Map()))))))))


{"statements":[]}

FulfilledFuture({"results":[],"errors":[]})



{"statements":[{"statement":"MATCH (n:ANIMAL {name: {name}}) return n","paramete
rs":{"name":"hippo"},"resultDataContents":["graph"]}]}

FulfilledFuture({"commit":"http://192.168.9.131:7474/db/data/transaction/307/com
mit","results":[{"columns":["n"],"data":[{"graph":{"nodes":[{"id":"118","labels"
:["ANIMAL"],"properties":{"name":"hippo"}}],"relationships":[]}}]}],"transaction
":{"expires":"Mon, 03 Oct 2016 13:13:30 +0000"},"errors":[]})

### extractGraphs
List(Result(List(n),List(Data(None,Some(Graph(List(Node(118,List(ANIMAL),Map(nam
e -> hippo))),List()))))))


{"statements":[{"statement":"match (V0) where id(V0) = {V0_itemId} set V0 += {V0
_propertyAdditions}   ","parameters":{"V0_itemId":118,"V0_propertyAdditions":{"n
ose":true}},"resultDataContents":["row","graph"]}]}

FulfilledFuture({"commit":"http://192.168.9.131:7474/db/data/transaction/307/com
mit","results":[{"columns":[],"data":[]}],"transaction":{"expires":"Mon, 03 Oct
2016 13:13:30 +0000"},"errors":[]})

### extractGraphs
List(Result(List(),List()))


{"statements":[]}

FulfilledFuture({"results":[],"errors":[]})



{"statements":[{"statement":"MATCH (n:ANIMAL {name: \"hippo\"}) OPTIONAL MATCH (
n)-[r]-() DELETE n,r"}]}

FulfilledFuture({"commit":"http://192.168.9.131:7474/db/data/transaction/308/com
mit","results":[{"columns":[],"data":[]}],"transaction":{"expires":"Mon, 03 Oct
2016 13:13:30 +0000"},"errors":[]})



{"statements":[{"statement":"MATCH (n:ANIMAL) OPTIONAL MATCH (n)-[r:EATS]->()\r\
n    RETURN n.name as name, COUNT(r) as eatcount","resultDataContents":["row"]}]
}

FulfilledFuture({"results":[{"columns":["name","eatcount"],"data":[{"row":["dog"
,0],"meta":[null,null]},{"row":["snake",2],"meta":[null,null]},{"row":["hippo",0
],"meta":[null,null]}]}],"errors":[]})


name    eatcount
dog     0
snake   2
hippo   0

hungriest: snake


{"statements":[{"statement":"MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r"}]}

 */





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