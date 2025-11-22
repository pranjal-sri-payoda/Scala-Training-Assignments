package services

import javax.inject.{Inject, Singleton}
import models.{Employee, EmployeeDAO}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeService @Inject()(employeeDAO: EmployeeDAO)(implicit ec: ExecutionContext) {

  def list(): Future[Seq[Employee]] = employeeDAO.list()

  def create(e: Employee): Future[Employee] = employeeDAO.insert(e)

  def get(id: Long): Future[Option[Employee]] = employeeDAO.getById(id)
}
