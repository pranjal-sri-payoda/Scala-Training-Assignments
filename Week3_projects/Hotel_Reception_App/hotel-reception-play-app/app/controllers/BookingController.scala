package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.BookingService
import models.Booking

import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

@Singleton
class BookingController @Inject()(
                                   cc: ControllerComponents,
                                   bookingService: BookingService
                                 )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  // ✔ Add Timestamp formatter FIRST
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsString(ts.toString)
    def reads(json: JsValue): JsResult[Timestamp] =
      json.validate[String].map(Timestamp.valueOf)
  }

  // ✔ Now you can derive Booking Format
  implicit val bookingFormat: OFormat[Booking] = Json.format[Booking]

  def list = Action.async {
    bookingService.list().map(b => Ok(Json.toJson(b)))
  }

  def get(id: Long) = Action.async {
    bookingService.find(id).map {
      case Some(booking) => Ok(Json.toJson(booking))
      case None          => NotFound(Json.obj("error" -> "Booking not found"))
    }
  }

  def create = Action.async(parse.json) { req =>
    val guestId = (req.body \ "guestId").as[Long]
    val roomId = (req.body \ "roomId").as[Long]
    val checkIn = Timestamp.valueOf((req.body \ "checkIn").as[String])
    val checkOut = Timestamp.valueOf((req.body \ "checkOut").as[String])
    val status = "CHECKED_IN"

    val booking = Booking(
      guestId = guestId,
      roomId = roomId,
      checkIn = checkIn,
      checkOut = checkOut,
      status = status,
      createdAt = Some(new Timestamp(System.currentTimeMillis()))
    )

    bookingService.create(booking).map {
      case Left(error) =>
        Conflict(Json.obj("error" -> error))
      case Right(saved) =>


        Created(Json.obj("message" -> "Booking created", "data" -> saved))
    }
  }


  def update(id: Long) = Action.async(parse.json) { request =>
    request.body.validate[Booking].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      booking =>
        bookingService.update(booking.copy(id = id))
          .map(_ => Ok(Json.toJson(booking.copy(id = id))))
    )
  }
}
