package kafka

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import scala.concurrent.{ExecutionContext, Future}
import spray.json._
import actors.ServiceCoordinatorActor

object KafkaBookingConsumer {

  private val Topic = "hotel_notifications"

  def run(
           coordinator: ActorRef[ServiceCoordinatorActor.Command]
         )(implicit system: ActorSystem[_]): Unit = {

    implicit val ec: ExecutionContext = system.executionContext

    val kafkaBootstrapServers: String =
      sys.env.getOrElse("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")

    println(s"🔗 Using Kafka Bootstrap Server: $kafkaBootstrapServers")

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(kafkaBootstrapServers)
        .withGroupId("booking-consumer-group-v2")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(Topic))
      .mapAsync(1) { msg =>
        val raw = msg.value()
        println(s"\n📥 Kafka Message Received:\n$raw\n")

        try {
          coordinator ! ServiceCoordinatorActor.ProcessBookingEvent(raw.parseJson)
        } catch {
          case _: Throwable => println(s"⚠️ Invalid JSON: $raw")
        }

        Future.successful(())
      }
      .runWith(Sink.ignore)
  }
}
