package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.VisitLogService
import models.VisitLog
import models.json.ModelFormats._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VisitLogController @Inject()(
                                    cc: ControllerComponents,
                                    visitLogService: VisitLogService
                                  )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // -------------------------------
  // GET: All logs
  // -------------------------------
  def list = Action.async {
    visitLogService.getAll.map(logs => Ok(Json.toJson(logs)))
  }

  // -------------------------------
  // GET: Logs for visitor
  // -------------------------------
  def getLogs(visitorId: Long) = Action.async {
    visitLogService.getLogsForVisitor(visitorId).map { logs =>
      Ok(Json.toJson(logs))
    }
  }
}
