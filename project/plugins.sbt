resolvers += Classpaths.sbtPluginReleases

// code coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")

// publishing
resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.12")
