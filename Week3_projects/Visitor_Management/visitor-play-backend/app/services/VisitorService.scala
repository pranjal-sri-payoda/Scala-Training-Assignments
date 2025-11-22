package services

import javax.inject.{Inject, Singleton}
import models.{Visitor, VisitorDAO}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VisitorService @Inject()(visitorDAO: VisitorDAO)(implicit ec: ExecutionContext) {

  def list(): Future[Seq[Visitor]] = visitorDAO.list()

  def get(id: Long): Future[Option[Visitor]] = visitorDAO.getById(id)

  def create(v: Visitor): Future[Visitor] = visitorDAO.insert(v)

  def delete(id: Long): Future[Int] = visitorDAO.delete(id)
}
