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

object KafkaVisitorConsumer {

  private val Topic = "visitor_notifications"

  def run(coordinator: ActorRef[ServiceCoordinatorActor.Command])
         (implicit system: ActorSystem[_]): Unit = {

    implicit val ec: ExecutionContext = system.executionContext

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId("visitor-consumer-group-v1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(Topic))
      .mapAsync(1) { msg =>
        val raw = msg.value()
        println(s"\n📥 Kafka Message Received:\n$raw\n")

        try {
          coordinator ! ServiceCoordinatorActor.ProcessVisitorEvent(raw.parseJson)
        } catch {
          case _: Throwable => println(s"⚠️ Invalid JSON: $raw")
        }

        Future.successful(())
      }
      .runWith(Sink.ignore)
  }
}
