name := "kafka-consumer-person"

version := "0.1.0"

scalaVersion := "2.13.12"

resolvers ++= Seq(
  "Akka Repository" at "https://repo.akka.io/releases/",
  Resolver.mavenCentral
)

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  "com.typesafe.akka" %% "akka-http" % "10.2.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.10",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.4.11"
)

ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-encoding", "UTF-8")