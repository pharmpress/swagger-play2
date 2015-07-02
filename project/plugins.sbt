resolvers ++= Seq(
    DefaultMavenRepository,
    "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)
//http://dl.bintray.com/typesafe/ivy-releases/com.typesafe.play/

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.0")
//libraryDependencies += "com.typesafe.play" %% "play" % "2.0-beta"
