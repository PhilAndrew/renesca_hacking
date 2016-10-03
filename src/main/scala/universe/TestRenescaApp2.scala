package universe

//: ----------------------------------------------------------------------------------
//: Copyright © 2016 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

//import renesca.parameter._
//import akka.actor.ActorSystem
// import renesca.parameter._
// import renesca.parameter.implicits._

import renesca.schema.macros._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import renesca.json.protocols.{ExampleJson, RequestJsonProtocol}
import renesca.{DbService, RestService}


@GraphSchema
object ExampleSchemaWrapping {
  // Nodes get their class name as uppercase label
  @Node class Animal { val name: String }
  @Node class Food {
    val name: String
    var amount: Long
  }
  // Relations get their class name as uppercase relationType
  @Relation class Eats(startNode: Animal, endNode: Food)
}

object TestRenescaApp2 extends App {

  ExampleJson.test()
/*
  // set up database connection
  val credentials = BasicHttpCredentials("neo4j", "password")
  // RestService contains an ActorSystem to handle HTTP communication via spray-client
  val restService = new RestService("http://192.168.9.131:7474", Some(credentials))

  // query interface for submitting single requests
  val db = new DbService
  // dependency injection
  db.restService = restService

  // only proceed if database is available and empty
  import scala.concurrent.duration._
  val wholeGraph = db.queryWholeGraph ///Await.result(db.queryWholeGraph, 30000 milliseconds)
  if (wholeGraph.nonEmpty) {
    //restService.actorSystem.shutdown()
    sys.error("Database is not empty.")
  }

  {
    import ExampleSchemaWrapping._
    val snake = Animal.create("snake")
    val cake = Food.create(name = "cake", amount = 1000)
    val eats = Eats.create(snake, cake)
    cake.amount -= 100
  }
  // clear database
  db.query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r")

  // shut down actor system
  //restService.actorSystem.shutdown()
*/
}

