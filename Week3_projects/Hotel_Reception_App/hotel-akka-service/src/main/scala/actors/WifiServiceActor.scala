package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object WifiServiceActor {

  sealed trait Command
  case class SendWifiCredentials(email: String, wifiUser: String, wifiPass: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {

        case SendWifiCredentials(email, wifiUser, wifiPass) =>
          println(s"[WiFiService] Sending WiFi credentials to $email")

          val subject = "WiFi Login Details"
          val body =
            s"""
               |Your WiFi login credentials:
               |
               |Username: $wifiUser
               |Password: $wifiPass
               |
               |Enjoy your stay!
               |""".stripMargin

          EmailHelper.sendEmail(email, subject, body)
          Behaviors.same
      }
    }
}
