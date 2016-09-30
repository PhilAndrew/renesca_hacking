
name := "renesca"

version := "1.4"

scalaVersion := "2.11.8"

//libraryDependencies += "io.spray" %% "spray-client" % "1.3.3"

//libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"

val akkaVersion = "2.4.10"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion

libraryDependencies += "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % akkaVersion

