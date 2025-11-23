package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

/**
 * GuestRegistryActor keeps an in-memory map of active guests and:
 *  - on GuestCheckedIn: stores guest and immediately asks restaurant actor to send one menu email
 *  - on GuestCheckedOut: removes guest (so scheduler won't include them)
 *  - on SendDailyMenus: iterates active guests and asks restaurant actor to send daily menus
 */
object GuestRegistryActor {

  sealed trait Command
  case class GuestCheckedIn(id: String, name:String, email: String, room: String) extends Command
  case class GuestCheckedOut(id: String, name: String) extends Command
  case object SendDailyMenus extends Command

  case class GuestInfo(name: String, email: String, room: String)

  def apply(restaurantService: ActorRef[RestaurantServiceActor.Command]): Behavior[Command] =
    Behaviors.setup { ctx =>
      var activeGuests = Map.empty[String, GuestInfo]

      Behaviors.receiveMessage {

        case GuestCheckedIn(id, name, email, room) =>
          println(s"[Registry] CHECK_IN RECEIVED for $name($id)")
          println(s"[Registry] Storing guest: id=$id, name=$name, email=$email, room=$room")

          activeGuests += (id -> GuestInfo(name, email, room))

          println(s"[Registry] Current active guests: ${activeGuests.keys.mkString(",")}")

          // --- SEND FIRST IMMEDIATE MENU EMAIL ---
          println(s"[Registry] Sending immediate menu for $name($id) (${email}) after check-in")
          restaurantService ! RestaurantServiceActor.SendMenu(name, email, room)

          Behaviors.same

        case GuestCheckedOut(id, name) =>
          activeGuests -= id
          println(s"[Registry] Guest $name($id) removed. Active guests = ${activeGuests.size}")
          Behaviors.same

        case SendDailyMenus =>
          println(s"[Registry] Triggered SendDailyMenus")
          println(s"[Registry] Active guests count: ${activeGuests.size}")

          activeGuests.foreach { case (id, info) =>
            println(s"[Registry] Sending menu request → $id (${info.email})")
            restaurantService ! RestaurantServiceActor.SendMenu(info.name, info.email, info.room)
          }

          Behaviors.same
      }
    }
}
