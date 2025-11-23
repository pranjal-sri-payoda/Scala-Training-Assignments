package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import spray.json._
import spray.json.DefaultJsonProtocol._

object ServiceCoordinatorActor {

  sealed trait Command
  case class ProcessBookingEvent(eventJson: JsValue) extends Command

  def apply(
             roomService: ActorRef[RoomServiceActor.Command],
             wifiService: ActorRef[WifiServiceActor.Command],
             restaurantRegistry: ActorRef[GuestRegistryActor.Command]
           ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {

        case ProcessBookingEvent(eventJson) =>
          println(s"[Coordinator] Raw event received: ${eventJson.prettyPrint}")

          val json     = eventJson.asJsObject
          println("[Coordinator] Parsed JSON")

          val eventType    = json.fields("event").convertTo[String]
          println(s"[Coordinator] Event type: $eventType")

          val guestObj = json.fields("guest").asJsObject
          println(s"[Coordinator] Guest JSON: ${guestObj.prettyPrint}")

          val roomObj  = json.fields("room").asJsObject
          println(s"[Coordinator] Room JSON: ${roomObj.prettyPrint}")


          val guestId   = guestObj.fields("id").toString().replace("\"", "")
          val fullName  = guestObj.fields.get("fullName").map(_.convertTo[String]).getOrElse("")
          val email     = guestObj.fields.get("email").map(_.convertTo[String]).getOrElse("")
          val roomNo    = roomObj.fields.get("roomNumber").map(_.convertTo[String]).getOrElse("")
          val category  = roomObj.fields.get("category").map(_.convertTo[String]).getOrElse("")


          println(s"[Coordinator] guestId=$guestId, guestName=$fullName, email=$email, roomNo=$roomNo")


          eventType match {

            case "CHECK_IN" =>
              println(s"[Coordinator] Received CHECK_IN for $fullName($guestId)")

              println("[Coordinator] Forwarding to RoomServiceActor...")
              roomService ! RoomServiceActor.SendWelcomeEmail(fullName, email, roomNo, category)

              println("[Coordinator] Forwarding to WifiServiceActor...")
              wifiService ! WifiServiceActor.SendWifiCredentials(email, s"user_$roomNo", "password123")

              // Forward to registry. Registry will store and trigger the immediate menu send.
              println("[Coordinator] Forwarding to GuestRegistryActor (will trigger immediate menu)...")
              restaurantRegistry ! GuestRegistryActor.GuestCheckedIn(guestId, fullName, email, roomNo)


            case "CHECK_OUT" =>
              println(s"[Coordinator] Guest $fullName($guestId) staying in room $roomNo has CHECKED OUT")
              restaurantRegistry ! GuestRegistryActor.GuestCheckedOut(guestId, fullName)

            case unknown =>
              println(s"[Coordinator] Ignoring unknown event: $unknown")
          }

          Behaviors.same
      }
    }
}
