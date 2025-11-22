package bootstrap

import akka.actor.typed.{Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration._
import kafka.KafkaBookingConsumer
import actors._
import java.time.{LocalDateTime, LocalTime, ZoneId}
import scala.jdk.DurationConverters._

object RootBehavior {
  sealed trait Command

  // compute initial delay until next hour:minute (local)
  private def initialDelayToNext(targetHour: Int, targetMinute: Int = 0): FiniteDuration = {
    val zone = ZoneId.systemDefault()
    val now = LocalDateTime.now(zone)
    val todayTarget = now.`with`(LocalTime.of(targetHour, targetMinute))
    val first = if (now.isBefore(todayTarget)) todayTarget else todayTarget.plusDays(1)
    val seconds = java.time.Duration.between(now, first).getSeconds
    seconds.seconds
  }

  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>

      // CHILD ACTORS
      val roomActor       = ctx.spawn(RoomServiceActor(), "RoomServiceActor")
      val wifiActor       = ctx.spawn(WifiServiceActor(), "WifiServiceActor")
      val restaurantActor = ctx.spawn(RestaurantServiceActor(), "RestaurantServiceActor")

      val guestRegistry   = ctx.spawn(GuestRegistryActor(restaurantActor), "GuestRegistryActor")

      val coordinator =
        ctx.spawn(ServiceCoordinatorActor(roomActor, wifiActor, guestRegistry), "ServiceCoordinator")

      // START KAFKA CONSUMER
      KafkaBookingConsumer.run(coordinator)(ctx.system)

      // DAILY MENU SCHEDULER
      // production: schedule for next 08:00 local time and then every 24 hours
      val initialDelay = initialDelayToNext(8, 0)
      val interval = 24.hours

      // For quick testing you can temporarily override:
      // val initialDelay = 10.seconds
      // val interval = 30.seconds

      println(s"⏳ Starting daily menu scheduler (first run in $initialDelay, interval = $interval)")
      import ctx.executionContext
      ctx.system.scheduler.scheduleAtFixedRate(
        initialDelay = initialDelay,
        interval = interval
      )(() => {
        println("⏰ Triggering daily menu scheduler...")
        guestRegistry ! GuestRegistryActor.SendDailyMenus
      })

      Behaviors.empty
    }
}
