package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models.RoomCategory
import services.RoomCategoryService
import java.sql.Timestamp

@Singleton
class RoomCategoryController @Inject()(
                                        cc: ControllerComponents,
                                        service: RoomCategoryService
                                      )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  // --- FIX: JSON formatter for java.sql.Timestamp ----
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsString(ts.toString)
    def reads(json: JsValue): JsResult[Timestamp] =
      json.validate[String].map(Timestamp.valueOf)
  }

  // --- Case class formatter ---
  implicit val fmt: OFormat[RoomCategory] = Json.format[RoomCategory]

  def list = Action.async {
    service.list().map(cats => Ok(Json.toJson(cats)))
  }
}
