package com.amazonaws

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

    //create new consumer
    val consumer = new  KafkaConsumer[String, Transaction](consumerProps)

    logger.info("About to start consuming messages from Kafka - Press CTRL-C to terminate.")
    Runtime.getRuntime.addShutdownHook(new Thread(() => consumer.close()))

    //subscribe to producer's topic
    consumer.subscribe(java.util.Arrays.asList("txns"))

    //poll for new messages every two seconds
    while(true) {
      val records = consumer.poll(500)

      //print each received record
      logger.debug(s"Fetched ${records.count()} records")

      //commit offsets on last poll
      consumer.commitSync()
    }
  }
}
