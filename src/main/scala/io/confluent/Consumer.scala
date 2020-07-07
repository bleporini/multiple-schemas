package io.confluent

import java.time.Duration
import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

import io.confluent.kafka.serializers.{AbstractKafkaSchemaSerDeConfig, KafkaAvroDeserializerConfig}
import io.confluent.samples.{Request, Response}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer}

import scala.collection.JavaConverters.{iterableAsScalaIterableConverter, seqAsJavaListConverter}

object Consumer extends App{

  val topicName= "topic1"
  val props = buildProperties
  val consumer = new KafkaConsumer[String, Any](props)
  consumer.subscribe(List(topicName).asJava)

  def consumeValue(record: ConsumerRecord[String, Any]): Unit = record.value() match {
    case request: Request => println(s"Request: $request")
    case response: Response => println(s"Response: $response")
    case value => println(s"Unknown value type ${value.getClass.getName}")
  }

  val shouldRun = new AtomicBoolean(true)

  Runtime.getRuntime.addShutdownHook(
    new Thread(() => shouldRun.set(false))
  )


  while (shouldRun.get()){
    consumer.poll(Duration.ofMillis(100)).asScala
      .foreach(r => consumeValue(r))
  }

  def buildProperties = {
    val properties = new Properties()
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test_consumer")
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer")
    properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8085")
    properties.put(AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY, "io.confluent.kafka.serializers.subject.RecordNameStrategy")
    properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
    properties
  }

}
