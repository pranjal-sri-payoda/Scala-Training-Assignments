package controllers

import models.Room

import javax.inject._
import play.api.mvc._
import services.RoomService

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import java.sql.Timestamp

@Singleton
class RoomController @Inject()(
                                cc: ControllerComponents,
                                roomService: RoomService
                              )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  // --- FIX: Timestamp formats ---
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsString(ts.toString)
    def reads(json: JsValue): JsResult[Timestamp] =
      json.validate[String].map(Timestamp.valueOf)
  }

  implicit val optionTimestampFormat: Format[Option[Timestamp]] =
    Format(
      Reads.optionWithNull[Timestamp],
      Writes.optionWithNull[Timestamp]
    )

  // --- Now this works ---
  implicit val roomFormat: OFormat[Room] = Json.format[Room]

  def list = Action.async {
    roomService.list().map(rooms => Ok(Json.toJson(rooms)))
  }
}
