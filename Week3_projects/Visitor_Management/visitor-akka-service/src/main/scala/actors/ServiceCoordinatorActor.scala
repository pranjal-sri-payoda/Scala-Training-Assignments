package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import spray.json._
import spray.json.DefaultJsonProtocol._

object ServiceCoordinatorActor {

  sealed trait Command
  case class ProcessVisitorEvent(eventJson: JsValue) extends Command

  def apply(
             hostActor: ActorRef[HostEmployeeActor.Command],
             itSupportActor: ActorRef[ITSupportActor.Command],
             securityActor: ActorRef[SecurityActor.Command]
           ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {

        case ProcessVisitorEvent(eventJson) =>
          println(s"[Coordinator] Event JSON:\n${eventJson.prettyPrint}")

          val json = eventJson.asJsObject
          val eventType = json.fields("event").convertTo[String]

          val visitor   = json.fields("visitor").asJsObject
          val visitorName  = visitor.fields.get("fullName").map(_.convertTo[String]).getOrElse("")
          val visitorEmail = visitor.fields.get("email").map(_.convertTo[String]).getOrElse("")
          val purpose      = visitor.fields.get("purpose").map(_.convertTo[String]).getOrElse("")

          // HOST IS OPTIONAL
          val hostOpt = json.fields.get("host").map(_.asJsObject)
          val hostName  = hostOpt.flatMap(_.fields.get("name")).map(_.convertTo[String]).getOrElse("")
          val hostEmail = hostOpt.flatMap(_.fields.get("email")).map(_.convertTo[String]).getOrElse("")

          eventType match {

            case "VISITOR_CHECK_IN" =>
              println("[Coordinator] Dispatching VISITOR_CHECK_IN")

              hostActor ! HostEmployeeActor.NotifyHost(hostName, hostEmail, visitorName, purpose)
              itSupportActor ! ITSupportActor.SendWifiCredentials(visitorEmail)
              securityActor ! SecurityActor.LogVisitorEntry(visitorName)

            case "VISITOR_CHECK_OUT" =>
              println("[Coordinator] Dispatching VISITOR_CHECK_OUT")

              securityActor ! SecurityActor.LogVisitorExit(visitorName)

            case unknown =>
              println(s"[Coordinator] Unknown event: $unknown")
          }

          Behaviors.same
      }
    }
}
