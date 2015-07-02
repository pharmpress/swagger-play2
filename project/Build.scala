import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  val appName = "swagger-play2"
  val appVersion = "1.0.1"
  val jacksonVersion = "1.9.13"
  val appDependencies = Seq(
    "org.json4s" % "json4s-core_2.10" % "3.2.10",
    "org.json4s" % "json4s-jackson_2.10" % "3.2.10",
    "org.codehaus.jackson" % "jackson-jaxrs" % jacksonVersion,
    "org.codehaus.jackson" % "jackson-xc" % jacksonVersion,
    "org.codehaus.jackson" % "jackson-mapper-asl" % jacksonVersion,
    "org.codehaus.jackson" % "jackson-core-asl" % jacksonVersion,
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "com.typesafe.play" %% "routes-compiler" % "2.2.0", //2.3.7
    "com.wordnik" % "swagger-core_2.9.1" % "1.0.1", //"1.3.12",
    "javax.ws.rs" % "jsr311-api" % "1.1.1")


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
    resolvers += Resolver.url("wordnik-remote-repos", new URL("https://ci.aws.wordnik.com/artifactory/libs-snapshots"))(Resolver.ivyStylePatterns),
    resolvers += "java-net" at "http://download.java.net/maven/2")
}
