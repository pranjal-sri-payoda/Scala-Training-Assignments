package controllers

import javax.inject._
import play.api.mvc._
import services.CustomerService
import play.api.libs.json._
import scala.concurrent.ExecutionContext

@Singleton
class CustomerController @Inject()(cc: ControllerComponents, service: CustomerService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getCustomer(id: Long) = Action.async {
    service.getCustomerDetails(id).map {
      case Some(resp) => Ok(Json.toJson(resp))
      case None       => NotFound(Json.obj("error" -> "Customer not found"))
    }
  }
}
