
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
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT",
      resolvers    := Seq(cakeRepo, confluentRepo),
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
    )),
    name := "kafka-trace",
  )

lazy val model = project
  .in(file("./model"))
  .settings(
    name := "model",
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue,
    libraryDependencies ++= Seq(
      avroSerializer
    )
  )

lazy val trafficSimulator = project
  .in(file("./traffic-simulator"))
  .settings(
    name := "traffic-simulator",
    mainClass := Some("com.amazonaws.StartTrafficSimulator"),
    libraryDependencies ++= Seq(
      avroSerializer,
      logback,
      scalaKafkaClient,
      scalaLogging,
      scallop,
      akkaTestKit % Test,
      scalaMock % Test,
      scalaTest % Test
    )
  )
  .dependsOn(model)

lazy val flinkConsumer = project
  .in(file("./flink-consumer"))
  .settings(
    name := "flink-consumer",
    mainClass := Some("com.amazonaws.FlinkConsumer"),
    libraryDependencies ++= Seq(
      akkaActor,
      akkaSlf4j,
      avroSerializer,
      awsS3,
      logback,
      scalaKafkaClient,
      scallop,
      akkaTestKit % Test,
      scalaMock % Test,
      scalaTest % Test
    )
  )
  .dependsOn(model)