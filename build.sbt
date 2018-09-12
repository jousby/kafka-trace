
// The file <project root>/project/Dependencies.scala contains a full list of all the AWS apis you can use in your
// libraryDependencies section below. You can also update the version of the AWS libs in this file as well.
import Resolvers._
import Dependencies._

// Project definition
lazy val root = project
  .in(file("."))
  .aggregate(model, trafficSimulator, flinkConsumer)
  .settings(
    inThisBuild(List(
      organization := "com.amazonaws",
      scalaVersion := "2.11.12",
      version      := "0.1.0-SNAPSHOT",
      resolvers    := Seq(cakeRepo, confluentRepo),
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
    )),
    name := "kafka-trace"
  )

lazy val model = project
  .in(file("./model"))
  .settings(
    name := "model",
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue,
    libraryDependencies ++= Seq(
      kafkaAvroSerializer
    )
  )

lazy val trafficSimulator = project
  .in(file("./traffic-simulator"))
  .settings(
    name := "traffic-simulator",
    mainClass := Some("com.amazonaws.StartTrafficSimulator"),
    libraryDependencies ++= Seq(
      kafkaAvroSerializer,
      logback,
      scalaKafkaClient,
      scalaLogging,
      scallop,
      akkaTestKit % Test,
      scalaMock % Test,
      scalaTest % Test
    ),
    excludeDependencies ++= Seq(
      "org.slf4j" % "slf4j-log4j12",
      "log4j" % "log4j"
    )
  )
  .dependsOn(model)

lazy val flinkConsumer = project
  .in(file("./flink-consumer"))
  .settings(
    name := "flink-consumer",
    mainClass := Some("com.amazonaws.StartFlinkConsumer"),
    libraryDependencies ++= Seq(
      avro4s,
      flink % Provided,
      flinkStreaming % Provided,
      flinkKafkaConnector,
      flinkAvroConfluentRegistry,
      logback,
      scalaLogging,
      scallop,
      akkaTestKit % Test,
      scalaMock % Test,
      scalaTest % Test
    ),
    excludeDependencies ++= Seq(
      "org.slf4j" % "slf4j-log4j12",
      "log4j" % "log4j"
    )
  )
  .dependsOn(model)