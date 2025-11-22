package bootstrap

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import kafka.KafkaVisitorConsumer
import actors._

object RootBehavior {

  sealed trait Command

  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>

      // Child actors
      val hostActor     = ctx.spawn(HostEmployeeActor(), "HostEmployeeActor")
      val itSupport     = ctx.spawn(ITSupportActor(), "ITSupportActor")
      val securityActor = ctx.spawn(SecurityActor(), "SecurityActor")

      // Coordinator
      val coordinator =
        ctx.spawn(ServiceCoordinatorActor(hostActor, itSupport, securityActor), "ServiceCoordinator")

      // Start Kafka Consumer
      KafkaVisitorConsumer.run(coordinator)(ctx.system)

      Behaviors.empty
    }
}
