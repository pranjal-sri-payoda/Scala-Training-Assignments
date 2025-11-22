package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class Guest(
                  id: Long = 0L,
                  fullName: String,
                  email: String,
                  phone: String,
                  address: Option[String] = None,
                  idProofPath: Option[String] = None,
                  createdAt: Option[Timestamp] = None,
                  updatedAt: Option[Timestamp] = None
                )

class GuestDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                        (implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class GuestTable(tag: Tag) extends Table[Guest](tag, "guests") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def fullName = column[String]("full_name")
    def email = column[String]("email")
    def phone = column[String]("phone")
    def address = column[Option[String]]("address")
    def idProofPath = column[Option[String]]("id_proof_path")
    def createdAt = column[Option[Timestamp]]("created_at")
    def updatedAt = column[Option[Timestamp]]("updated_at")

    def * =
      (id, fullName, email, phone, address, idProofPath, createdAt, updatedAt)
        .<>((Guest.apply _).tupled, Guest.unapply)
  }

  val guests = TableQuery[GuestTable]

  def list(): Future[Seq[Guest]] =
    db.run(guests.result)

  def getById(id: Long): Future[Option[Guest]] =
    db.run(guests.filter(_.id === id).result.headOption)

  def insert(g: Guest): Future[Guest] =
    db.run(
      (guests returning guests.map(_.id)
        into ((guest, id) => guest.copy(id = id))
        ) += g
    )

  def update(id: Long, name: String, phone: String): Future[Int] =
    db.run(
      guests
        .filter(_.id === id)
        .map(g => (g.fullName, g.phone))
        .update((name, phone))
    )

  def delete(id: Long): Future[Int] =
    db.run(guests.filter(_.id === id).delete)
}
