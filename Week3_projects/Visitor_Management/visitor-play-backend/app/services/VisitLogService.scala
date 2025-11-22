package services

import javax.inject.{Inject, Singleton}
import models.{VisitLog, VisitLogDAO}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VisitLogService @Inject()(visitLogDAO: VisitLogDAO)(implicit ec: ExecutionContext) {

  def getAll: Future[Seq[VisitLog]] =
    visitLogDAO.getAll

  def getLogsForVisitor(visitorId: Long): Future[Seq[VisitLog]] =
    visitLogDAO.getByVisitor(visitorId)

  // --------------------------
  // New Methods Required
  // --------------------------

  def checkIn(visitorId: Long, hostEmployeeId: Long): Future[Long] =
    visitLogDAO.createVisitLog(visitorId, hostEmployeeId)

  def getActiveVisit(visitorId: Long): Future[Option[VisitLog]] =
    visitLogDAO.getActiveVisitByVisitor(visitorId)

  def markCheckout(visitId: Long): Future[Int] =
    visitLogDAO.markAsCheckedOut(visitId)
}
