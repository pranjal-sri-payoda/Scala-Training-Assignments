name := """visitor-play-backend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  guice,
  filters,

  "com.typesafe.play" %% "play-json" % "2.9.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,

  "org.playframework" %% "play-slick"            % "6.1.0",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0",
  "mysql" % "mysql-connector-java" % "8.0.26",

  "org.apache.kafka" % "kafka-clients" % "3.5.1",

  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-stream"      % "2.8.5",
  "com.typesafe.akka" %% "akka-slf4j"       % "2.8.5"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
