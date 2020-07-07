/**
  * Copyright 2020 Confluent Inc.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package io.confluent

import java.lang.System.currentTimeMillis
import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.samples.{Request, Response}
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.errors.TopicExistsException

import scala.util.Try

object Producer extends App {
  val topicName = "topic1"
  val props = buildProperties
  createTopic(topicName, 1, 3, props)
  val producer = new KafkaProducer[String, Any](props)

  val callback = new Callback {
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
      Option(exception) match {
        case Some(err) => println(s"Failed to produce: $err")
        case None =>  println(s"Produced record at $metadata")
      }
    }
  }

  for( i <- 1 to 10) {
    val now = currentTimeMillis()
    val requestId = "testId" + now
    val request = Request
      .newBuilder()
      .setRequestId(requestId)
      .setTimestamp(now)
      .build()
    val response = Response
      .newBuilder()
      .setRequestId(requestId)
      .setTimestamp(now + 2)
      .setResponse("OK")
      .build()
    val recordRequest = new ProducerRecord[String, Any](topicName, "test", request)
    val recordResponse = new ProducerRecord[String, Any](topicName, "test", response)
    producer.send(recordRequest, callback)
    producer.send(recordResponse, callback)
  }
  producer.flush()
  producer.close()
  println("Wrote ten records to " + topicName)

  def buildProperties = {
    val properties: Properties = new Properties
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer")
    properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8085")
    properties.put(AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY, "io.confluent.kafka.serializers.subject.RecordNameStrategy")
    properties
  }

  def createTopic(topic: String, partitions: Int, replication: Int, cloudConfig: Properties): Unit = {
    val newTopic = new NewTopic(topic, partitions, replication.toShort)
    val adminClient = AdminClient.create(cloudConfig)
    Try (adminClient.createTopics(Collections.singletonList(newTopic)).all.get).recover {
      case e :Exception =>
        // Ignore if TopicExistsException, which may be valid if topic exists
        if (!e.getCause.isInstanceOf[TopicExistsException]) throw new RuntimeException(e)
    }
    adminClient.close()
  }
}
