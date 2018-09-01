package com.amazonaws

import java.time.Instant
import java.time.temporal.{ChronoField, ChronoUnit}
import java.util.{Properties, UUID}

import com.amazonaws.model.Transaction
import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.rogach.scallop.{ScallopConf, ScallopOption}


trait MessageGenerator[T] {
  def create(): T
}

class TrafficSimulator[T](producerProps: Properties, topic: String, messageGenerator: MessageGenerator[T]) extends LazyLogging {

  lazy val producer = new KafkaProducer[String, T](producerProps)
  var stopping = false

  def start() = {
    var msgCounter = 0
    var lastReportingTime = Instant.now()

    while (true && !stopping) {
      producer.send(new ProducerRecord(topic, messageGenerator.create()))
      msgCounter += 1
      if (msgCounter % 1000000 == 0) {
        val now = Instant.now()
        val millis = ChronoUnit.MILLIS.between(lastReportingTime, now)
        lastReportingTime = now
        logger.debug(s"Published 1M mesg in $millis millis")
      }
    }
  }

  def stop() = {
    logger.info(s"Stopping Traffic Simulator for topic: $topic")
    stopping = true
    producer.close()
  }
}

object StartTrafficSimulator {

  // model command line arguemnts
  private class Args(args: Array[String]) extends ScallopConf(args) {
    val kafkaUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:9092"))
    val registryUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:8081"))
    verify()
  }

  def main(args: Array[String]): Unit = {

    val cl = new Args(args)

    val producerProps: Properties = new Properties()

    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cl.kafkaUrl.toOption.get)
    producerProps.put("schema.registry.url", cl.registryUrl.toOption.get)

    val txnSimulator = new TrafficSimulator[Transaction](producerProps, "txns", TransactionGenerator)

    println("About to start publishing messages to Kafka - Press CTRL-C to terminate.")
    Runtime.getRuntime.addShutdownHook(new Thread(() => txnSimulator.stop()))

    txnSimulator.start()
  }
}
