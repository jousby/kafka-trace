package com.amazonaws

import java.time.Instant
import java.util.Properties

import com.amazonaws.model.Transaction
import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.collection.JavaConverters._


object FlinkConsumer extends LazyLogging {

  // model command line arguments
  private class Args(args: Array[String]) extends ScallopConf(args) {
    val kafkaUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:9092"))
    val registryUrl: ScallopOption[String] = opt[String](required = true, default = Some("http://localhost:8081"))
    verify()
  }

  def main(args: Array[String]): Unit = {

    val cl = new Args(args)

    val consumerProps: Properties = new Properties()

    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getCanonicalName)
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cl.kafkaUrl.toOption.get)
    consumerProps.put("schema.registry.url", cl.registryUrl.toOption.get)

    //set group id and set specific avro reader to true
    consumerProps.put("group.id", "txns-consumer-group")
    consumerProps.put("specific.avro.reader", "true")

    var stopping = false
    //create new consumer
    val consumer = new  KafkaConsumer[String, Transaction](consumerProps)

    logger.info("About to start consuming messages from Kafka - Press CTRL-C to terminate.")
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      stopping = true
      consumer.close()
    }))

    //subscribe to producer's topic
    consumer.subscribe(java.util.Arrays.asList("txns"))

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

    //poll for new messages every two seconds
    while(!stopping) {
      val records = consumer.poll(500)
      msgCounter += records.count()

      //print each received record
      //logger.debug(s"Fetched ${records.count()} records")

      //commit offsets on last poll
      consumer.commitSync()
    }
  }
}
