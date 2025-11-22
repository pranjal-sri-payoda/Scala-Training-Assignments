package services

import javax.inject._
import models.{Room, RoomDAO}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomService @Inject()(
                             roomDAO: RoomDAO
                           )(implicit ec: ExecutionContext) {

  def list(): Future[Seq[Room]] = roomDAO.list()

  def find(id: Long): Future[Option[Room]] = roomDAO.findById(id)

  def create(room: Room): Future[Room] = roomDAO.insert(room)

  def update(room: Room): Future[Int] = roomDAO.update(room)

  def setAvailability(roomId: Long, available: Boolean): Future[Int] =
    roomDAO.setAvailability(roomId, available)

  def availableRooms(): Future[Seq[Room]] =
    roomDAO.getAvailableRooms()
}
