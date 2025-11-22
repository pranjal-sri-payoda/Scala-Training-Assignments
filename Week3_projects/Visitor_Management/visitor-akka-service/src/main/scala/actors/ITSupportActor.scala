package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object ITSupportActor {

  sealed trait Command
  case class SendWifiCredentials(visitorEmail: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case SendWifiCredentials(visitorEmail) =>
          println(s"[ITSupport] Sending WiFi credentials to $visitorEmail")

          val subject = "Temporary WiFi Access"
          val body =
            s"""
               |Your WiFi credentials:
               |Username: temp_visitor
               |Password: welcome123
               |""".stripMargin

          EmailHelper.sendEmail(visitorEmail, subject, body)
          Behaviors.same
      }
    }
}
