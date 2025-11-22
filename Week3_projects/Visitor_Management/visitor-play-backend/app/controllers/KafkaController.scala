package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.KafkaProducerService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaController @Inject()(
                                 cc: ControllerComponents,
                                 kafkaProducerService: KafkaProducerService
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /** Send VISITOR CHECK-IN event */
  def sendVisitorCheckIn = Action.async(parse.json) { req =>
    val visitorId = (req.body \ "visitorId").as[Long]

    kafkaProducerService.processVisitorCheckIn(visitorId).map {
      case Left(error) =>
        NotFound(Json.obj("error" -> error))

      case Right(payload) =>
        Ok(Json.obj(
          "message" -> "Visitor CHECK-IN Kafka event sent",
          "kafkaPayload" -> payload
        ))
    }
  }

  /** Send VISITOR CHECK-OUT event */
  def sendVisitorCheckOut = Action.async(parse.json) { req =>
    val visitorId = (req.body \ "visitorId").as[Long]

    kafkaProducerService.processVisitorCheckOut(visitorId).map {
      case Left(error) =>
        NotFound(Json.obj("error" -> error))

      case Right(payload) =>
        Ok(Json.obj(
          "message" -> "Visitor CHECK-OUT Kafka event sent",
          "kafkaPayload" -> payload
        ))
    }
  }
}
