package exercises

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

// Correct ScalaPB import
import events.UserEvent.UserEvent

object LocalProducer {

  def main(args: Array[String]): Unit = {

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

    val producer = new KafkaProducer[String, Array[Byte]](props)

    try {
      (1 to 100).foreach { i =>
        val event = UserEvent(
          userId = s"user-${i % 10}",
          action = if (i % 2 == 0) "click" else "view",
          value = i.toDouble
        )

        val record = new ProducerRecord[String, Array[Byte]](
          "user-events",
          event.userId,
          event.toByteArray
        )

        producer.send(record)
      }

      println("Sent 100 UserEvent messages.")
    } finally {
      producer.close()
    }
  }
}
