
name := "renesca"

version := "1.5"

scalaVersion := "2.11.8"

val paradiseVersion = "2.1.0"

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

// START: This part is to use Renesca Magic to compile macros and should not create any output to the bundle.
// http://stackoverflow.com/questions/21515325/add-a-compile-time-only-dependency-in-sbt
ivyConfigurations += config("compileonly").hide

libraryDependencies ++= Seq(
  "com.github.renesca" % "renesca-magic_2.11" % "0.3.4-1" % "compileonly"
)

// appending everything from 'compileonly' to unmanagedClasspath
unmanagedClasspath in Compile ++=
  update.value.select(configurationFilter("compileonly"))

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