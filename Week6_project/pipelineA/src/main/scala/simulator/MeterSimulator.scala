package simulator

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import model.{WaterReading, JsonProtocol}

import scala.util.Random

object MeterSimulator {

  case object GenerateReading

  private def randomDouble(min: Double, max: Double): Double =
    min + (Random.nextDouble() * (max - min))

  def apply(
             meterId: String,
             householdId: Int,
             kafkaProducer: ActorRef[String]
           ): Behavior[GenerateReading.type] =
    Behaviors.setup { ctx =>
      Behaviors.receiveMessage {

        case GenerateReading =>
          val now = System.currentTimeMillis()
          val hour = java.time.LocalDateTime.now().getHour

          // Base consumption 5–25 L (replacement for Random.between)
          val base = randomDouble(5.0, 25.0)

          val timeAdjusted =
            if (hour >= 7 && hour <= 9) base * 1.5                // morning peak
            else if (hour >= 18 && hour <= 21) base * 1.4         // evening peak
            else if (hour >= 0 && hour <= 5) base * 0.4           // night low
            else base

          // 2% anomalies
          val finalConsumption =
            if (Random.nextDouble() < 0.02)
              timeAdjusted * randomDouble(3.0, 6.0)               // spike
            else
              timeAdjusted

          // Pressure = 2.0 ± 0.3
          val pressure = 2.0 + randomDouble(-0.3, 0.3)

          val reading = WaterReading(
            meter_id = meterId,
            household_id = householdId,
            consumption_liters = finalConsumption,
            pressure = pressure,
            device_status = "OK",
            timestamp = now
          )

          val json = JsonProtocol.toJson(reading)
          kafkaProducer ! json

          ctx.log.info(s"[Meter $meterId] Sent reading: $json")

          Behaviors.same
      }
    }
}
