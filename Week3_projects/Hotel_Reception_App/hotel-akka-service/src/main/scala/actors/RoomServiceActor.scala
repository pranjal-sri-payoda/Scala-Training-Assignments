package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object RoomServiceActor {

  sealed trait Command
  case class SendWelcomeEmail(name: String, email: String, roomNo: String, category: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case SendWelcomeEmail(name, email, roomNo, category) =>
          println(s"[RoomService] Sending welcome email to $email")

          val subject = s"Welcome to Our Hotel - Room $roomNo"
          val body =
            s"""
               |Dear $name,
               |
               |Welcome to our hotel! We are delighted to have you stay with us.
               |
               |Your room details are as follows:
               |• Room Number: $roomNo
               |• Category: $category
               |
               |Useful Numbers:
               |• Emergency: 999
               |• Room Service: 202
               |
               |If you need anything at all, feel free to reach out.
               |
               |We wish you a pleasant and comfortable stay!
               |
               |Warm regards,
               |Hotel Guest Services
               |""".stripMargin

          EmailHelper.sendEmail(email, subject, body)
          Behaviors.same
      }
    }
}
