package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.GuestService
import models.Guest
import scala.concurrent.ExecutionContext
import java.sql.Timestamp

@Singleton
class GuestController @Inject()(
                                 cc: ControllerComponents,
                                 guestService: GuestService
                               )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  // Timestamp formatter MUST be before Json.format
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = JsString(t.toString)
    def reads(json: JsValue): JsResult[Timestamp] =
      json.validate[String].map(Timestamp.valueOf)
  }

  implicit val guestFormat: OFormat[Guest] = Json.format[Guest]

  def list = Action.async {
    guestService.list().map(guests => Ok(Json.toJson(guests)))
  }

  def get(id: Long) = Action.async {
    guestService.getById(id).map {
      case Some(g) => Ok(Json.toJson(g))
      case None    => NotFound(Json.obj("error" -> "Guest not found"))
    }
  }

  def create = Action.async(parse.json) { req =>
    val name = (req.body \ "name").as[String]
    val email = (req.body \ "email").as[String]
    val phone = (req.body \ "phone").as[String]
    val idProof = (req.body \ "idProofPath").as[String]

    guestService.create(name, email, phone, idProof).map { g =>
      Created(Json.obj("message" -> "Guest created", "data" -> g))
    }
  }

  def update(id: Long) = Action.async(parse.json) { req =>
    val name = (req.body \ "name").as[String]
    val phone = (req.body \ "phone").as[String]

    guestService.update(id, name, phone).map {
      case true  => Ok(Json.obj("message" -> "Guest updated"))
      case false => NotFound(Json.obj("error" -> "Guest not found"))
    }
  }

  def delete(id: Long) = Action.async {
    guestService.delete(id).map {
      case true  => Ok(Json.obj("message" -> "Guest deleted"))
      case false => NotFound(Json.obj("error" -> "Guest not found"))
    }
  }
}
