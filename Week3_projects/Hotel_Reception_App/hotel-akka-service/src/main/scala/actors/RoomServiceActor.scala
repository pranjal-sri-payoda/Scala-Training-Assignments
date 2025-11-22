package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object RoomServiceActor {

  sealed trait Command
  case class SendWelcomeEmail(email: String, roomNo: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {

        case SendWelcomeEmail(email, roomNo) =>
          println(s"[RoomService] Sending welcome email to $email")

          val subject = s"Welcome to the Hotel - Room $roomNo"
          val body =
            s"""
               |Dear Guest,
               |
               |Welcome to our hotel!
               |Your room number is $roomNo.
               |
               |Emergency: 999
               |Room Service: 202
               |
               |Enjoy your stay!
               |""".stripMargin

          EmailHelper.sendEmail(email, subject, body)
          Behaviors.same
      }
    }
}
