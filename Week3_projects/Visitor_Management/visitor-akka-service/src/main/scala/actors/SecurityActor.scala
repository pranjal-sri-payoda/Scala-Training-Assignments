package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior

object SecurityActor {

  sealed trait Command
  case class LogVisitorEntry(visitorName: String) extends Command
  case class LogVisitorExit(visitorName: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case LogVisitorEntry(visitorName) =>
          println(s"[Security] Visitor ENTERED: $visitorName")

        case LogVisitorExit(visitorName) =>
          println(s"[Security] Visitor EXITED: $visitorName")
      }

      Behaviors.same
    }
}
