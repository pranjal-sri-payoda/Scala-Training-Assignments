package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.DepartmentService
import models.Department
import models.json.ModelFormats._

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DepartmentController @Inject()(
                                      cc: ControllerComponents,
                                      departmentService: DepartmentService
                                    )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def list = Action.async {
    departmentService.list().map(d => Ok(Json.toJson(d)))
  }

  def create = Action.async(parse.json) { req =>
    val now = new Timestamp(System.currentTimeMillis())
    val name = (req.body \ "name").as[String]

    val department = Department(
      name = name,
      createdAt = Some(now),
      updatedAt = Some(now)
    )

    departmentService.create(department).map { saved =>
      Created(Json.obj(
        "message" -> "Department created",
        "data" -> saved
      ))
    }
  }

  def get(id: Long) = Action.async {
    departmentService.get(id).map {
      case Some(dep) => Ok(Json.toJson(dep))
      case None => NotFound(Json.obj("error" -> "Department not found"))
    }
  }
}
