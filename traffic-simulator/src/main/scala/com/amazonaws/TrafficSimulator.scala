package com.amazonaws

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Properties

import com.amazonaws.model.Transaction
import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.language.implicitConversions


trait MessageGenerator[T] {
  def create(): T
}

class TrafficSimulator[T](producerProps: Properties, topic: String, messageGenerator: MessageGenerator[T], targetTps: Int = 0) extends LazyLogging {

  lazy val producer = new KafkaProducer[String, T](producerProps)
  var stopping = false
  val MILLIS_IN_SECOND = 1000
  val UNLIMITED = 0

  def start() = startTraffic(targetTps > UNLIMITED)

  def stop() = {
    logger.info(s"Stopping Traffic Simulator for topic: $topic")
    stopping = true
    producer.close()
  }

  def startTraffic(rateLimited: Boolean): Unit = {
    var msgCounter = 0
    var targetSecond = Instant.now.getEpochSecond + 1
    var msgWatermark = msgCounter

    while (!stopping) {
      producer.send(new ProducerRecord(topic, messageGenerator.create()))
      msgCounter += 1

      // check if we are limiting the rate of emission and sleep if we have hit our target
      if (rateLimited && ((msgCounter - msgWatermark) == targetTps))
        while (Instant.now.getEpochSecond < targetSecond) Thread.sleep(1)

      // if the second has ticked over report and update counters
      if (Instant.now.getEpochSecond >= targetSecond) {
        val msgsThisSecond = msgCounter - msgWatermark
        if (msgsThisSecond < targetTps)
          logger.warn(s"$msgsThisSecond tps (short of $targetTps)")
        else
          logger.info(s"$msgsThisSecond tps")
        msgWatermark = msgCounter
        targetSecond = Instant.now.getEpochSecond + 1
      }
    }

    logger.info(s"$msgCounter messages sent")
  }
}

object StartTrafficSimulator {

  // model command line arguemnts
  private class Args(args: Array[String]) extends ScallopConf(args) {
    val kafkaUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:9092"))
    val registryUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:8081"))
    val targetTps: ScallopOption[Int] = opt[Int](required = true, default = Some(0))
    verify()
  }

  implicit def map2Properties(map: Map[String, String]): java.util.Properties = {
    (new java.util.Properties /: map) { case (props, (k, v)) => props.put(k, v); props }
  }

  def main(args: Array[String]): Unit = {

    println(java.net.InetAddress.)
    // process command line args
    val commandLine = new Args(args)

    // kafka producer config
    val kafkaProducerProperties = Map(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer].getCanonicalName,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer].getCanonicalName,
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> commandLine.kafkaUrl.toOption.get,
      "schema.registry.url" -> commandLine.registryUrl.toOption.get
    )

    // create the traffic simulator, this uses the passed in TransactionGenerate to create dummy messages
    val txnTrafficSimulator = new TrafficSimulator[Transaction](
      kafkaProducerProperties,
      "txns",
      TransactionGenerator,
      commandLine.targetTps.toOption.get
    )

    println("About to start publishing messages to Kafka - Press CTRL-C to terminate.")
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        txnTrafficSimulator.stop()
      }
    })

    txnTrafficSimulator.start()
  }
}
