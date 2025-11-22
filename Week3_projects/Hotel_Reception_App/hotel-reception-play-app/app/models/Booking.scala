package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

case class Booking(
                    id: Long = 0L,
                    guestId: Long,
                    roomId: Long,
                    checkIn: Timestamp,
                    checkOut: Timestamp,
                    status: String,
                    createdAt: Option[Timestamp] = None,
                    updatedAt: Option[Timestamp] = None
                  )

class BookingDAO @Inject()(
                            dbConfigProvider: DatabaseConfigProvider
                          )(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  // ------------------------------
  // Local lightweight Guest reference
  // ------------------------------
  class GuestRefTable(tag: Tag) extends Table[Long](tag, "guests") {
    def id = column[Long]("id", O.PrimaryKey)
    def * = id
  }
  private val guestRef = TableQuery[GuestRefTable]

  // ------------------------------
  // Local lightweight Room reference
  // ------------------------------
  class RoomRefTable(tag: Tag) extends Table[Long](tag, "rooms") {
    def id = column[Long]("id", O.PrimaryKey)
    def * = id
  }
  private val roomRef = TableQuery[RoomRefTable]

  // ------------------------------
  // REAL Booking table
  // ------------------------------
  class BookingTable(tag: Tag) extends Table[Booking](tag, "bookings") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def guestId = column[Long]("guest_id")
    def roomId = column[Long]("room_id")
    def checkIn = column[Timestamp]("check_in")
    def checkOut = column[Timestamp]("check_out")
    def status = column[String]("status")
    def createdAt = column[Option[Timestamp]]("created_at")
    def updatedAt = column[Option[Timestamp]]("updated_at")

    // ----------- FIXED WORKING FOREIGN KEYS -----------
    def guestFk =
      foreignKey("fk_booking_guest", guestId, guestRef)(_.id)

    def roomFk =
      foreignKey("fk_booking_room", roomId, roomRef)(_.id)

    def * =
      (id, guestId, roomId, checkIn, checkOut, status, createdAt, updatedAt)
        .<>((Booking.apply _).tupled, Booking.unapply)
  }

  val bookings = TableQuery[BookingTable]

  // ------------------------------
  // CRUD methods
  // ------------------------------
  def list(): Future[Seq[Booking]] =
    db.run(bookings.result)

  def findById(id: Long): Future[Option[Booking]] =
    db.run(bookings.filter(_.id === id).result.headOption)

  def insert(b: Booking): Future[Booking] =
    db.run(
      (bookings returning bookings.map(_.id)
        into ((booking, id) => booking.copy(id = id))
        ) += b
    )

  def update(b: Booking): Future[Int] =
    db.run(bookings.filter(_.id === b.id).update(b))

  def delete(id: Long): Future[Int] =
    db.run(bookings.filter(_.id === id).delete)

  def markAsCheckedOut(bookingId: Long): Future[Int] = {
    val now = new Timestamp(System.currentTimeMillis())

    db.run(
      bookings
        .filter(_.id === bookingId)
        .map(b => (b.status, b.updatedAt))
        .update(("CHECKED_OUT", Some(now)))
    )
  }

}
