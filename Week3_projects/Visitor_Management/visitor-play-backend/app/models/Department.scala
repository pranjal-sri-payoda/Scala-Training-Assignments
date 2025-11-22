package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class Department(
                       id: Long = 0L,
                       name: String,
                       createdAt: Option[Timestamp] = None,
                       updatedAt: Option[Timestamp] = None
                     )

class DepartmentDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                             (implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class DepartmentTable(tag: Tag) extends Table[Department](tag, "departments") {
    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name      = column[String]("name")
    def createdAt = column[Option[Timestamp]]("created_at")
    def updatedAt = column[Option[Timestamp]]("updated_at")

    def * = (id, name, createdAt, updatedAt).<>(Department.tupled, Department.unapply)
  }

  val departments = TableQuery[DepartmentTable]

  def list() = db.run(departments.result)
  def getById(id: Long) = db.run(departments.filter(_.id === id).result.headOption)

  def insert(dep: Department): Future[Department] =
    db.run(
      (departments returning departments.map(_.id)
        into ((d, id) => d.copy(id = id))
        ) += dep
    )
}
