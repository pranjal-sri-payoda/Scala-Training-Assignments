package kafka

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties

object KafkaProducerActor {

  def apply(topic: String, bootstrap: String): Behavior[String] =
    Behaviors.setup { ctx =>

      val props = new Properties()
      props.put("bootstrap.servers", bootstrap)
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

      val producer = new KafkaProducer[String, String](props)

      ctx.log.info(s"Kafka Producer started → Topic: $topic, Bootstrap: $bootstrap")

      Behaviors.receiveMessage { json =>
        val record = new ProducerRecord[String, String](topic, json)
        producer.send(record)
        Behaviors.same
      }
    }
}
