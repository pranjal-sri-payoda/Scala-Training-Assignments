package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object RestaurantServiceActor {

  sealed trait Command
  case class SendMenu(email: String, roomNo: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {

        case SendMenu(email, roomNo) =>
          println(s"[Restaurant] Received SendMenu command for $email, room = $roomNo")

          val subject = "Today's Restaurant Menu"
          val body =
            s"""
               |Good morning!
               |
               |Today's menu:
               |🍲 Soup
               |🥘 Paneer Masala
               |🍛 Rice
               |🍞 Roti
               |🍮 Dessert
               |
               |Room: $roomNo
               |
               |Have a wonderful day!
               |""".stripMargin

          println("[Restaurant] Sending email through EmailHelper...")
          EmailHelper.sendEmail(email, subject, body)
          println("[Restaurant] EmailHelper completed")

          Behaviors.same
      }
    }
}
