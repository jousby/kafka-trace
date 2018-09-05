package com.amazonaws

import java.util.concurrent.TimeUnit

import com.amazonaws.model.Transaction
import com.sksamuel.avro4s.{FromRecord, RecordFormat, ToRecord}
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.streaming.api.windowing.time._
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.rogach.scallop.{ScallopConf, ScallopOption}
import org.apache.avro.generic.GenericRecord

import scala.language.implicitConversions

object StartFlinkConsumer extends LazyLogging {

  // model command line arguments
  private class Args(args: Array[String]) extends ScallopConf(args) {
    val kafkaUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:9092"))
    val registryUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:8081"))
    verify()
  }

  implicit def map2Properties(map: Map[String, String]): java.util.Properties = {
    (new java.util.Properties /: map) { case (props, (k, v)) => props.put(k, v); props }
  }

  def main(args: Array[String]): Unit = {

    val commandLine = new Args(args)

    val kafkaConsumerProperties = Map(
       ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> commandLine.kafkaUrl.toOption.get
    )

    val txnDeserializer = ConfluentRegistryAvroDeserializationSchema.forGeneric(
      Transaction.SCHEMA$,
      commandLine.registryUrl.toOption.get
    )

    val kafkaConsumer: FlinkKafkaConsumer011[GenericRecord] = new FlinkKafkaConsumer011(
      "txns", txnDeserializer, kafkaConsumerProperties
    )

    logger.info("About to start consuming messages from Kafka - Press CTRL-C to terminate.")
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        kafkaConsumer.close()
      }
    })

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val genericStream: DataStream[GenericRecord] = env.addSource(kafkaConsumer)

    // transform our DataStream[GenericRecord] into our target type DataStream[Transaction]
    val stream: DataStream[Transaction] = genericStream.map(RecordFormat[Transaction].from(_))

    // print how many objects are being processed in a 1 second window
    stream
      .map(v => 1)
      .timeWindowAll(Time.of(1, TimeUnit.SECONDS))
      .sum(0)
      .map(count => s"$count tps")
      .print()

    // start the program
    env.execute()
  }
}
