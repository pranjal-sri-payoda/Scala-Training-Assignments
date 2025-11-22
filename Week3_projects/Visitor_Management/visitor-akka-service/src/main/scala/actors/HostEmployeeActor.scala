package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object HostEmployeeActor {

  sealed trait Command
  case class NotifyHost(hostName: String, hostEmail: String, visitorName: String, purpose: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case NotifyHost(hostName, hostEmail, visitorName, purpose) =>
          println(s"[HostActor] Email to host $hostEmail")

          val subject = "Visitor Arrival Notification"
          val body =
            s"""
               |Hi $hostName,
               |
               |Your visitor $visitorName has arrived at reception for $purpose
               |""".stripMargin

          EmailHelper.sendEmail(hostEmail, subject, body)
          Behaviors.same
      }
    }
}
