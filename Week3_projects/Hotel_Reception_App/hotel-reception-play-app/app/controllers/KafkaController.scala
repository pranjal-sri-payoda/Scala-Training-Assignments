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
                               )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def sendBookingEvent = Action.async(parse.json) { req =>
    val bookingId = (req.body \ "bookingId").as[Long]

    kafkaProducerService.processBookingEvent(bookingId).map {
      case Left(error) =>
        NotFound(Json.obj("error" -> error))

      case Right(msg) =>
        Ok(Json.obj(
          "message" -> "Kafka message sent successfully",
          "kafkaPayload" -> msg
        ))
    }
  }

  def sendCheckoutEvent = Action.async(parse.json) { req =>
    val bookingId = (req.body \ "bookingId").as[Long]

    kafkaProducerService.processCheckoutEvent(bookingId).map {
      case Left(error) =>
        NotFound(Json.obj("error" -> error))

      case Right(msg) =>
        Ok(Json.obj(
          "message" -> "Guest checked out & Kafka event sent",
          "kafkaPayload" -> msg
        ))
    }
  }

}
