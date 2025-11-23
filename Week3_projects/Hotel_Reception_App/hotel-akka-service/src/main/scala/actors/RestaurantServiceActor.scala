package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import utils.EmailHelper

object RestaurantServiceActor {

  sealed trait Command
  case class SendMenu(name: String, email: String, roomNo: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case SendMenu(name, email, roomNo) =>
          println(s"[Restaurant] Received SendMenu command for $email, room = $roomNo")

          val subject = "Today's Restaurant Menu"
          val body =
            s"""
               |Dear $name,
               |
               |Good morning! ☀️
               |
               |Here is today's delicious restaurant menu:
               |
               |🍲 Soup of the Day
               |🥘 Paneer Masala
               |🍛 Steamed Basmati Rice
               |🍞 Tandoori Roti
               |🍮 Dessert of the Day
               |
               |Your Room: $roomNo
               |
               |We hope you enjoy your meals and have a wonderful day ahead!
               |
               |Warm regards,
               |Hotel Restaurant Team
               |""".stripMargin

          EmailHelper.sendEmail(email, subject, body)
          Behaviors.same
      }
    }
}
