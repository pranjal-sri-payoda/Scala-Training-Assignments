package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.{VisitorService, VisitLogService}
import models.Visitor
import models.json.ModelFormats._
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

@Singleton
class VisitorController @Inject()(
                                   cc: ControllerComponents,
                                   visitorService: VisitorService,
                                   visitLogService: VisitLogService        // <-- ADDED
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def list = Action.async {
    visitorService.list().map(v => Ok(Json.toJson(v)))
  }

  def get(id: Long) = Action.async {
    visitorService.get(id).map {
      case Some(visitor) => Ok(Json.toJson(visitor))
      case None => NotFound(Json.obj("error" -> "Visitor not found"))
    }
  }

  // ------------------------------------------------------
  // ✔ CREATE VISITOR + CHECK-IN ENTRY IN visit_logs
  // ------------------------------------------------------
  def create = Action.async(parse.json) { req =>
    val now = new Timestamp(System.currentTimeMillis())

    val fullName = (req.body \ "fullName").as[String]
    val email    = (req.body \ "email").as[String]
    val phone    = (req.body \ "phone").as[String]

    val purpose        = (req.body \ "purpose").asOpt[String]
    val hostEmployeeId = (req.body \ "hostEmployeeId").asOpt[Long]
    val idProof        = (req.body \ "idProof").asOpt[String]

    val visitor = Visitor(
      fullName = fullName,
      email = email,
      phone = phone,
      purpose = purpose,
      hostEmployeeId = hostEmployeeId,
      idProof = idProof,
      createdAt = Some(now),
      updatedAt = Some(now)
    )

    for {
      saved <- visitorService.create(visitor)

      // Insert check-in visit log ONLY if hostEmployeeId exists
      _ <- hostEmployeeId match {
        case Some(empId) =>
          visitLogService.checkIn(saved.id, empId)   // <-- CHECK-IN HAPPENS HERE
        case None =>
          Future.successful(())
      }

    } yield {
      Created(Json.obj(
        "message" -> "Visitor created & check-in logged",
        "data" -> saved
      ))
    }
  }

  def delete(id: Long) = Action.async {
    visitorService.delete(id).map(rows => Ok(Json.obj("deleted" -> rows)))
  }
}
