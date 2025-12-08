import scalapb.compiler.Version.scalapbVersion

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.13"

// FORCE protoc version so it works on macOS ARM (M1/M2/M3)
ThisBuild / PB.protocVersion := "3.21.12"

lazy val root = (project in file("."))
  .settings(
    name := "scala-pb",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.1",
      "org.apache.spark" %% "spark-sql" % "3.5.1",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1",
      "org.apache.spark" %% "spark-protobuf" % "3.5.1",

      // ScalaPB runtime
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapbVersion,

      // Kafka client for producer
      "org.apache.kafka" % "kafka-clients" % "3.5.1"
    ),

    // Enable ScalaPB code generation
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    ),

    // Ensure src/main/protobuf is included automatically
    Compile / PB.protoSources += file("src/main/protobuf")
  )
