package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class RoomCategory(
                         id: Long = 0L,
                         name: String,
                         description: Option[String] = None,
                         pricePerNight: BigDecimal,
                         createdAt: Option[Timestamp] = None,
                         updatedAt: Option[Timestamp] = None
                       )

object RoomCategory {
  import slick.jdbc.MySQLProfile.api._

  class RoomCategoryTable(tag: Tag) extends Table[RoomCategory](tag, "room_categories") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[Option[String]]("description")
    def pricePerNight = column[BigDecimal]("price_per_night")
    def createdAt = column[Option[Timestamp]]("created_at")
    def updatedAt = column[Option[Timestamp]]("updated_at")

    def * = (
      id,
      name,
      description,
      pricePerNight,
      createdAt,
      updatedAt
    ) <> ((RoomCategory.apply _).tupled, RoomCategory.unapply)
  }
}

class RoomCategoryDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._
  import RoomCategory._

  private val categories = TableQuery[RoomCategoryTable]

  def list(): Future[Seq[RoomCategory]] =
    db.run(categories.result)

  def findById(id: Long): Future[Option[RoomCategory]] =
    db.run(categories.filter(_.id === id).result.headOption)

  def insert(cat: RoomCategory): Future[RoomCategory] = db.run {
    (categories returning categories.map(_.id)
      into ((c, id) => c.copy(id = id))
      ) += cat
  }

  def update(cat: RoomCategory): Future[Int] =
    db.run(categories.filter(_.id === cat.id).update(cat))

  def delete(id: Long): Future[Int] =
    db.run(categories.filter(_.id === id).delete)
}
