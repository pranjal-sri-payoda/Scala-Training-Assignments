name := """smart-water-play-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
// Play JSON (MUST use this for Play 2.9)
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC7"
libraryDependencies ++= Seq(
//  filters,

  "org.playframework" %% "play-slick"            % "6.1.0",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0",
  "mysql" % "mysql-connector-java" % "8.0.26",

  "com.auth0" % "java-jwt" % "4.3.0",
)

// AWS S3 SDK (v1, simpler for Play services)
libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.12.626"

// Avro Reader
libraryDependencies += "org.apache.avro" % "avro" % "1.12.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.2",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.17.2",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.17.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.2"
)
libraryDependencies += "org.xerial.snappy" % "snappy-java" % "1.1.10.5"
