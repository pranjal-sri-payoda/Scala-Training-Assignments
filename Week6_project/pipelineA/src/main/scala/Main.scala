import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import com.typesafe.config.ConfigFactory
import kafka.KafkaProducerActor
import simulator.MeterSimulator

import scala.concurrent.duration._

object Main extends App {

  val config = ConfigFactory.load()

  val numMeters     = config.getInt("simulator.num-meters")
  val interval      = config.getInt("simulator.event-interval-seconds")
  val kafkaTopic    = config.getString("simulator.kafka.topic")
  val kafkaBootstrap = config.getString("simulator.kafka.bootstrap")

  val system = ActorSystem(Behaviors.setup[Unit] { ctx =>

    val producer = ctx.spawn(
      KafkaProducerActor(kafkaTopic, kafkaBootstrap),
      "kafka-producer"
    )

    ctx.log.info(s"Starting $numMeters smart water meters…")

    (1 to numMeters).foreach { id =>
      val meterActor = ctx.spawn(
        MeterSimulator(s"WMTR-$id", id, producer),
        s"meter-$id"
      )

      ctx.system.scheduler.scheduleAtFixedRate(
        initialDelay = 0.seconds,
        interval = interval.seconds
      )(() => meterActor ! MeterSimulator.GenerateReading)(ctx.executionContext)
    }

    Behaviors.empty

  }, "SmartWaterMeterSimulator")
}
