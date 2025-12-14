package controllers

import javax.inject._
import play.api.mvc._
import services.{HouseholdService, BillingService}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models.HouseholdModels._    // ✅ REQUIRED JSON FORMATTERS

@Singleton
class HouseholdController @Inject()(
                                     cc: ControllerComponents,
                                     householdService: HouseholdService,
                                     billingService: BillingService
                                   )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getRecentReadings(id: Long, limit: Int) = Action {
    val data = householdService.getRecentReadings(id, limit)
    Ok(Json.toJson(data))
  }

  def getDailyUsage(householdId: Long) = Action.async {
    householdService.getDailyUsage(householdId).map { result =>
      Ok(Json.toJson(result))
    }
  }


  def getBillingHistory(id: Long) = Action.async {
    billingService.getBills(id).map { bills =>
      Ok(Json.toJson(bills))
    }
  }
}
