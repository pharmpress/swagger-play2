
name := "swagger-play2"

version := "1.0.1"

scalaVersion := "2.11.6"


resolvers ++= Seq(
  Resolver.file("Local Ivy Repository", file(Path.userHome.absolutePath+"/.ivy2/local"))(Resolver.ivyStylePatterns) ,
  Resolver.url("wordnik-remote-repos", new URL("https://ci.aws.wordnik.com/artifactory/libs-snapshots"))(Resolver.ivyStylePatterns),
  "Pharmpress everything" at "http://rpsci.rps.local:8081/nexus/content/groups/everything/",
  "java-net" at "http://download.java.net/maven/2"
)

libraryDependencies ++= Seq(
    "org.codehaus.jackson" % "jackson-jaxrs" % "1.8.5",
    "org.codehaus.jackson" % "jackson-xc" % "1.8.5",
    "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.5",
    "org.codehaus.jackson" % "jackson-core-asl" % "1.8.5",
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "com.wordnik" %% "swagger-core" % "1.3.12",
    "javax.ws.rs" % "jsr311-api" % "1.1.1"
)