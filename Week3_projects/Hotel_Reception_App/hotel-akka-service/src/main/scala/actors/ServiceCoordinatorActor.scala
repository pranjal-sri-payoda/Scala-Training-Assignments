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

          val json = eventJson.asJsObject
          println("[Coordinator] Parsed JSON")

          val eventType = json.fields("event").convertTo[String]
          println(s"[Coordinator] Event type: $eventType")

          val guestObj = json.fields("guest").asJsObject
          println(s"[Coordinator] Guest JSON: ${guestObj.prettyPrint}")

          val guestId = guestObj.fields("id").convertTo[String]
          val email   = guestObj.fields("email").convertTo[String]
          val roomNo  = json.fields("room").asJsObject.fields("roomNumber").convertTo[String]

          println(s"[Coordinator] guestId=$guestId, email=$email, roomNo=$roomNo")


          eventType match {

            case "CHECK_IN" =>
              println(s"[Coordinator] Received CHECK_IN for $guestId")

              println("[Coordinator] Forwarding to RoomServiceActor...")
              roomService ! RoomServiceActor.SendWelcomeEmail(email, roomNo)

              println("[Coordinator] Forwarding to WifiServiceActor...")
              wifiService ! WifiServiceActor.SendWifiCredentials(email, s"user_$roomNo", "password123")

              // Forward to registry. Registry will store and trigger the immediate menu send.
              println("[Coordinator] Forwarding to GuestRegistryActor (will trigger immediate menu)...")
              restaurantRegistry ! GuestRegistryActor.GuestCheckedIn(guestId, email, roomNo)


            case "CHECK_OUT" =>
              println(s"[Coordinator] Guest $guestId CHECKED OUT")
              restaurantRegistry ! GuestRegistryActor.GuestCheckedOut(guestId)

            case unknown =>
              println(s"[Coordinator] Ignoring unknown event: $unknown")
          }

          Behaviors.same
      }
    }
}
