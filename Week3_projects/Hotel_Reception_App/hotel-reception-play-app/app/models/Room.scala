package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class Room(
                 id: Long = 0L,
                 roomNumber: String,
                 floor: Int,
                 categoryId: Long,
                 isAvailable: Boolean = true,
                 createdAt: Option[Timestamp] = None,
                 updatedAt: Option[Timestamp] = None
               )

object Room {
  import slick.jdbc.MySQLProfile.api._
  import RoomCategory._

  class RoomTable(tag: Tag) extends Table[Room](tag, "rooms") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def roomNumber = column[String]("room_number")
    def floor = column[Int]("floor")
    def categoryId = column[Long]("category_id")
    def isAvailable = column[Boolean]("is_available")
    def createdAt = column[Option[Timestamp]]("created_at")
    def updatedAt = column[Option[Timestamp]]("updated_at")

    def categoryFk = foreignKey(
      "fk_room_category",
      categoryId,
      TableQuery[RoomCategoryTable]
    )(_.id, onDelete = ForeignKeyAction.Cascade)

    def * = (id, roomNumber, floor, categoryId, isAvailable, createdAt, updatedAt) <> ((Room.apply _).tupled, Room.unapply)
  }
}

class RoomDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._
  import Room._

  private val rooms = TableQuery[RoomTable]

  def list(): Future[Seq[Room]] = db.run(rooms.result)

  def findById(id: Long): Future[Option[Room]] =
    db.run(rooms.filter(_.id === id).result.headOption)

  def getAvailableRooms(): Future[Seq[Room]] =
    db.run(rooms.filter(_.isAvailable === true).result)

  def insert(room: Room): Future[Room] = db.run {
    (rooms returning rooms.map(_.id)
      into ((r, id) => r.copy(id = id))
      ) += room
  }

  def update(room: Room): Future[Int] =
    db.run(rooms.filter(_.id === room.id).update(room))

  def setAvailability(id: Long, available: Boolean): Future[Int] =
    db.run(rooms.filter(_.id === id).map(_.isAvailable).update(available))

  def isAvailable(roomId: Long): Future[Boolean] =
    db.run(rooms.filter(_.id === roomId).map(_.isAvailable).result.head)
      .map(identity)

  def markRoomAvailable(roomId: Long): Future[Int] = {
    val now = new Timestamp(System.currentTimeMillis())

    db.run(
      rooms
        .filter(_.id === roomId)
        .map(r => (r.isAvailable, r.updatedAt))
        .update((true, Some(now)))
    )
  }

}
