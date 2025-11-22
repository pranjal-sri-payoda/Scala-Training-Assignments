package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class Visitor(
                    id: Long = 0L,
                    fullName: String,
                    email: String,
                    phone: String,
                    purpose: Option[String] = None,
                    hostEmployeeId: Option[Long] = None,
                    idProof: Option[String] = None,
                    createdAt: Option[Timestamp] = None,
                    updatedAt: Option[Timestamp] = None
                  )

class VisitorDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                          (implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  /** -----------------------------
   *  Lightweight EMPLOYEE REF TABLE
   *  ----------------------------- */
  class EmployeeRefTable(tag: Tag) extends Table[(Long)](tag, "employees") {
    def id = column[Long]("id", O.PrimaryKey)
    def * = id
  }
  private val employees = TableQuery[EmployeeRefTable]

  /** -----------------------------
   *  VISITOR TABLE
   *  ----------------------------- */
  class VisitorTable(tag: Tag) extends Table[Visitor](tag, "visitors") {

    def id            = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def fullName      = column[String]("full_name")
    def email         = column[String]("email")
    def phone         = column[String]("phone")
    def purpose       = column[Option[String]]("purpose")
    def hostEmployeeId = column[Option[Long]]("host_employee_id")
    def idProof       = column[Option[String]]("id_proof")
    def createdAt     = column[Option[Timestamp]]("created_at")
    def updatedAt     = column[Option[Timestamp]]("updated_at")

    // Proper FK handling for nullable Long
    def hostEmployeeFk =
      foreignKey("fk_visitor_employee", hostEmployeeId, employees)(_.id.?)

    def * =
      (id, fullName, email, phone, purpose, hostEmployeeId, idProof, createdAt, updatedAt)
        .<>(Visitor.tupled, Visitor.unapply)
  }

  val visitors = TableQuery[VisitorTable]

  /** -----------------------------
   *  DAO METHODS
   *  ----------------------------- */

  def list(): Future[Seq[Visitor]] =
    db.run(visitors.result)

  def getById(id: Long): Future[Option[Visitor]] =
    db.run(visitors.filter(_.id === id).result.headOption)

  def insert(v: Visitor): Future[Visitor] =
    db.run(
      (visitors returning visitors.map(_.id)
        into ((visitor, id) => visitor.copy(id = id))
        ) += v
    )

  def delete(id: Long): Future[Int] =
    db.run(visitors.filter(_.id === id).delete)
}
