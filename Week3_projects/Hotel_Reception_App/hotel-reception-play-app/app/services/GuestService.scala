package services

import models.{GuestDAO, Guest}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

@Singleton
class GuestService @Inject()(guestDAO: GuestDAO)(implicit ec: ExecutionContext) {

  def list(): Future[Seq[Guest]] =
    guestDAO.list()

  def getById(id: Long): Future[Option[Guest]] =
    guestDAO.getById(id)

  def create(name: String, email: String, phone: String, idProofPath: String): Future[Guest] = {
    val guest = Guest(
      fullName = name,
      email = email,
      phone = phone,
      idProofPath = Some(idProofPath),
      createdAt = Some(new Timestamp(System.currentTimeMillis()))
    )
    guestDAO.insert(guest)
  }

  def update(id: Long, name: String, phone: String): Future[Boolean] =
    guestDAO.update(id, name, phone).map(_ > 0)

  def delete(id: Long): Future[Boolean] =
    guestDAO.delete(id).map(_ > 0)
}
