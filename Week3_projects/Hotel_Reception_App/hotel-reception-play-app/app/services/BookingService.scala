package services

import javax.inject._
import models.{Booking, BookingDAO, RoomDAO}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookingService @Inject()(
                                bookingDAO: BookingDAO,
                                roomDAO: RoomDAO
                              )(implicit ec: ExecutionContext) {

  def list(): Future[Seq[Booking]] = bookingDAO.list()

  def find(id: Long): Future[Option[Booking]] = bookingDAO.findById(id)

  def create(booking: Booking): Future[Either[String, Booking]] =
    for {
      available <- roomDAO.isAvailable(booking.roomId)
      result <- if (!available) {
        Future.successful(Left("Room is not available for booking"))
      } else {
        bookingDAO.insert(booking).flatMap { saved =>
          roomDAO.setAvailability(saved.roomId, false).map(_ => Right(saved))
        }
      }
    } yield result


  def update(booking: Booking): Future[Int] =
    bookingDAO.update(booking)
}
