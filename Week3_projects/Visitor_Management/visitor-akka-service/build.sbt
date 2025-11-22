ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "visitor-akka-service"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed"     % "2.8.5",
  "com.typesafe.akka" %% "akka-stream"          % "2.8.5",
  "com.typesafe.akka" %% "akka-slf4j"           % "2.8.5",
  "com.typesafe.akka" %% "akka-http"            % "10.5.0",
  "com.typesafe.akka" %% "akka-stream-typed"    % "2.8.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",

  // Akka-based consumers
  "com.typesafe.akka" %% "akka-stream-kafka"    % "3.0.0",

  // Kafka client dependency
  "org.apache.kafka"  %  "kafka-clients"        % "3.5.1",

  // Email support
  "com.sun.mail"      % "jakarta.mail"          % "2.0.1"
)
