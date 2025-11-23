package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object WifiServiceActor {

  sealed trait Command
  case class SendWifiCredentials(email: String, wifiUser: String, wifiPass: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case SendWifiCredentials(email, wifiUser, wifiPass) =>
          println(s"[WiFiService] Sending WiFi credentials to $email")

          val subject = "Your WiFi Login Details"
          val body =
            s"""
               |Hello,
               |
               |Here are your WiFi login details:
               |
               |• Username: $wifiUser
               |• Password: $wifiPass
               |
               |You may connect your devices anytime.
               |
               |If you face any issues, please contact the front desk.
               |
               |Regards,
               |Hotel IT Support
               |""".stripMargin

          EmailHelper.sendEmail(email, subject, body)
          Behaviors.same
      }
    }
}
