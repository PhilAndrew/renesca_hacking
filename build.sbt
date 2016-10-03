
name := "renesca"

version := "1.4"

scalaVersion := "2.11.8"

val paradiseVersion = "2.1.0"

libraryDependencies += "com.github.renesca" %% "renesca-magic" % "0.3.4-1"

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)


//libraryDependencies += "io.spray" %% "spray-client" % "1.3.3"

//libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"

val akkaVersion = "2.4.10"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion

//libraryDependencies += "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % akkaVersion

val Json4sVersion = "3.4.1"

libraryDependencies += "org.json4s" %% "json4s-native" % Json4sVersion

libraryDependencies += "org.json4s" %% "json4s-ext" % Json4sVersion

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"))


scalacOptions ++= scalacOpts

autoCompilerPlugins := true

val scalacOpts = (
  "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-Yinline" :: "-Yinline-warnings" ::
    "-language:_" ::
    // "-Xdisable-assertions" :: "-optimize" ::
    Nil
  )


val scalacMacroOpts = (
  "-Ymacro-debug-lite" ::
    "-Yshow-trees-stringified" ::
    Nil
  )