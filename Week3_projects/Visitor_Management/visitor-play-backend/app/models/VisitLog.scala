package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class VisitLog(
                     id: Long = 0L,
                     visitorId: Long,
                     hostEmployeeId: Long,
                     checkIn: Timestamp,
                     checkOut: Option[Timestamp] = None
                   )

class VisitLogDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                           (implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  // ---------- Reference Tables ----------
  class VisitorRefTable(tag: Tag) extends Table[Long](tag, "visitors") {
    def id = column[Long]("id", O.PrimaryKey)
    def * = id
  }
  private val visitorRef = TableQuery[VisitorRefTable]

  class EmployeeRefTable(tag: Tag) extends Table[Long](tag, "employees") {
    def id = column[Long]("id", O.PrimaryKey)
    def * = id
  }
  private val employeeRef = TableQuery[EmployeeRefTable]

  // ---------- Visit Logs ----------
  class VisitLogTable(tag: Tag) extends Table[VisitLog](tag, "visit_logs") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def visitorId = column[Long]("visitor_id")
    def hostEmployeeId = column[Long]("host_employee_id")
    def checkIn = column[Timestamp]("check_in")
    def checkOut = column[Option[Timestamp]]("check_out")

    def visitorFk =
      foreignKey("fk_visitlog_visitor", visitorId, visitorRef)(_.id)

    def hostEmployeeFk =
      foreignKey("fk_visitlog_employee", hostEmployeeId, employeeRef)(_.id)

    def * = (id, visitorId, hostEmployeeId, checkIn, checkOut)
      .<>((VisitLog.apply _).tupled, VisitLog.unapply)
  }

  val visitLogs = TableQuery[VisitLogTable]

  // --------------------------
  // READ METHODS
  // --------------------------

  def getAll: Future[Seq[VisitLog]] =
    db.run(visitLogs.sortBy(_.id.desc).result)

  def getByVisitor(visitorId: Long): Future[Seq[VisitLog]] =
    db.run(visitLogs.filter(_.visitorId === visitorId).result)

  // --------------------------
  // WRITE METHODS (Required)
  // --------------------------

  /** Create a new check-in log */
  def createVisitLog(visitorId: Long, hostEmployeeId: Long): Future[Long] = {
    val log = VisitLog(
      visitorId = visitorId,
      hostEmployeeId = hostEmployeeId,
      checkIn = new Timestamp(System.currentTimeMillis())
    )

    db.run(
      (visitLogs returning visitLogs.map(_.id)) += log
    )
  }

  /** Get the active (not checked out) visit for a visitor */
  def getActiveVisitByVisitor(visitorId: Long): Future[Option[VisitLog]] = {
    db.run(
      visitLogs
        .filter(v => v.visitorId === visitorId && v.checkOut.isEmpty)
        .result
        .headOption
    )
  }

  /** Mark the visit as checked out */
  def markAsCheckedOut(visitId: Long): Future[Int] = {
    val checkoutTime = Some(new Timestamp(System.currentTimeMillis()))
    db.run(
      visitLogs
        .filter(_.id === visitId)
        .map(_.checkOut)
        .update(checkoutTime)
    )
  }
}
