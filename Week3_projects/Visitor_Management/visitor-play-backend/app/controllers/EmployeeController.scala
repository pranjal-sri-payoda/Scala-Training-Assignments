package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.EmployeeService
import models.Employee
import models.json.ModelFormats._

import java.sql.Timestamp     // ✅ REQUIRED
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeController @Inject()(
                                    cc: ControllerComponents,
                                    employeeService: EmployeeService
                                  )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def list = Action.async {
    employeeService.list().map(e => Ok(Json.toJson(e)))
  }

  def create = Action.async(parse.json) { req =>
    val now = new Timestamp(System.currentTimeMillis())  // ✅ now compiles

    val fullName = (req.body \ "fullName").as[String]
    val email = (req.body \ "email").as[String]
    val phone = (req.body \ "phone").as[String]
    val departmentId = (req.body \ "departmentId").as[Long]

    val emp = Employee(
      fullName = fullName,
      email = email,
      phone = phone,
      departmentId = departmentId,
      createdAt = Some(now),
      updatedAt = Some(now)
    )

    employeeService.create(emp).map { saved =>
      Created(Json.obj(
        "message" -> "Employee created",
        "data" -> saved
      ))
    }
  }

  def get(id: Long) = Action.async {
    employeeService.get(id).map {
      case Some(emp) => Ok(Json.toJson(emp))
      case None => NotFound(Json.obj("error" -> "Employee not found"))
    }
  }
}
