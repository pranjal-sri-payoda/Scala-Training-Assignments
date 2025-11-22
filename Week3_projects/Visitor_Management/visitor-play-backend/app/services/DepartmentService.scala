package services

import javax.inject.{Inject, Singleton}
import models.{Department, DepartmentDAO}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DepartmentService @Inject()(departmentDAO: DepartmentDAO)(implicit ec: ExecutionContext) {

  def list(): Future[Seq[Department]] = departmentDAO.list()

  def create(dep: Department): Future[Department] = departmentDAO.insert(dep)

  def get(id: Long): Future[Option[Department]] = departmentDAO.getById(id)
}
