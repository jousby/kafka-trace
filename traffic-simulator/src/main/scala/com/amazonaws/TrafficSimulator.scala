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

class TrafficSimulator[T](producerProps: Properties, topic: String, messageGenerator: MessageGenerator[T], targetTps: Int = 0) extends LazyLogging {

  lazy val producer = new KafkaProducer[String, T](producerProps)
  var stopping = false
  val MILLIS_IN_SECOND = 1000
  val UNLIMITED = 0

  def start() = if (targetTps == UNLIMITED) unlimitedTraffic() else rateLimitedTraffic(targetTps)

  def stop() = {
    logger.info(s"Stopping Traffic Simulator for topic: $topic")
    stopping = true
    producer.close()
  }

  def rateLimitedTraffic(targetTps: Int): Unit = {
    logger.info(s"TrafficSimulator target rate set to $targetTps per second")
    var msgCounter = 0
    var lastReportingTime = Instant.now()

    while (!stopping) {
      producer.send(new ProducerRecord(topic, messageGenerator.create()))
      msgCounter += 1
      if (msgCounter == targetTps) {
        val now = Instant.now()
        val elapsedMillis = ChronoUnit.MILLIS.between(lastReportingTime, now)
        val secondDelta = MILLIS_IN_SECOND - elapsedMillis
        val messagesPerSecond: Long = (msgCounter * (MILLIS_IN_SECOND.toDouble / elapsedMillis.toDouble)).toLong
        lastReportingTime = now
        msgCounter = 0

        if (secondDelta > 0) {
          logger.debug(s"Rate limited to the target tps of $targetTps")
          val targetSecond = now.getEpochSecond + 1

          // Sleep until the next second
          while (Instant.now.getEpochSecond < targetSecond) Thread.sleep(1)
          lastReportingTime = Instant.now()
        }
        else {
          logger.warn(s"Current tps is $messagesPerSecond, unable to achieve the target tps of $targetTps")
        }
      }
    }
    logger.info(s"$msgCounter messages sent")
  }

  def unlimitedTraffic(): Unit = {
    var msgCounter = 0

    // attempts to report metrics once per second
    val metricsObvserver = new Thread() {
      override def run(): Unit = {
        logger.info(s"TrafficSimulator target rate set to unlimited")
        var msgWatermark = msgCounter
        var targetSecond = Instant.now.getEpochSecond + 1
        while (!stopping) {
          // sleep until the next reporting second
          while (Instant.now.getEpochSecond < targetSecond) Thread.sleep(1)

          // second has ticked over lets report
          val msgCounterSnap = msgCounter
          logger.info(s"Current tps ${msgCounterSnap - msgWatermark}")
          msgWatermark = msgCounterSnap
          targetSecond = targetSecond + 1
        }
      }
    }

    metricsObvserver.start()

    while (!stopping) {
      producer.send(new ProducerRecord(topic, messageGenerator.create()))
      msgCounter += 1
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

  def main(args: Array[String]): Unit = {

    val cl = new Args(args)

    val producerProps: Properties = new Properties()

    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cl.kafkaUrl.toOption.get)
    producerProps.put("schema.registry.url", cl.registryUrl.toOption.get)

    val txnSimulator = new TrafficSimulator[Transaction](producerProps, "txns", TransactionGenerator, cl.targetTps.toOption.get)

    println("About to start publishing messages to Kafka - Press CTRL-C to terminate.")
    Runtime.getRuntime.addShutdownHook(new Thread(() => txnSimulator.stop()))

    txnSimulator.start()
  }
}
