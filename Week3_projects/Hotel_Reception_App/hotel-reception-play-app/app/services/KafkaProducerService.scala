package services

import javax.inject._
import models.{Booking, BookingDAO, Guest, GuestDAO, Room, RoomDAO, RoomCategory, RoomCategoryDAO}
import play.api.libs.json._

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaProducerService @Inject()(
                                      bookingDAO: BookingDAO,
                                      guestDAO: GuestDAO,
                                      roomDAO: RoomDAO,
                                      roomCategoryDAO: RoomCategoryDAO
                                    )(implicit ec: ExecutionContext) {

  // ---------------------------------------
  // JSON formats (MANDATORY for Json.toJson)
  // ---------------------------------------

  implicit val timestampFormat: Format[java.sql.Timestamp] = new Format[java.sql.Timestamp] {
    def writes(ts: java.sql.Timestamp) = JsString(ts.toString)
    def reads(json: JsValue) = json.validate[String].map(java.sql.Timestamp.valueOf)
  }

  implicit val bookingFormat: OFormat[Booking] = Json.format[Booking]
  implicit val guestFormat: OFormat[Guest]     = Json.format[Guest]
  implicit val roomFormat: OFormat[Room]       = Json.format[Room]
  implicit val categoryFormat: OFormat[RoomCategory] = Json.format[RoomCategory]

  // ---------------------------------------
  // Kafka config
  // ---------------------------------------
  private val topic = "hotel_notifications"

  private val props = new java.util.Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  // ---------------------------------------
  // CHECK_IN LOGIC
  // ---------------------------------------
  def processBookingEvent(bookingId: Long): Future[Either[String, JsValue]] =
    for {
      bookingOpt <- bookingDAO.findById(bookingId)
      result <- bookingOpt match {

        case None =>
          Future.successful(Left("Booking not found"))

        case Some(booking) =>
          for {
            guestOpt <- guestDAO.getById(booking.guestId)
            roomOpt  <- roomDAO.findById(booking.roomId)
          } yield {

            if (guestOpt.isEmpty) Left("Guest not found")
            else if (roomOpt.isEmpty) Left("Room not found")
            else {

              val guest = guestOpt.get
              val room  = roomOpt.get

              val payload = Json.obj(
                "event" -> "CHECK_IN",
                "guest" -> Json.obj(
                  "id" -> guest.id.toString,
                  "email" -> guest.email
                ),
                "room" -> Json.obj(
                  "roomNumber" -> room.roomNumber
                )
              )

              println("Sending CHECK-IN event: " + payload)

              producer.send(new ProducerRecord[String, String](topic, payload.toString()))
              Right(payload)
            }
          }
      }
    } yield result

  // ---------------------------------------
  // CHECK_OUT LOGIC
  // ---------------------------------------
  def processCheckoutEvent(bookingId: Long): Future[Either[String, JsValue]] =
    for {
      bookingOpt <- bookingDAO.findById(bookingId)
      result <- bookingOpt match {

        case None =>
          Future.successful(Left("Booking not found"))

        case Some(booking) =>
          for {
            guestOpt <- guestDAO.getById(booking.guestId)
          } yield {

            if (guestOpt.isEmpty) Left("Guest not found")
            else {

              // marking room free
              bookingDAO.markAsCheckedOut(bookingId)
              roomDAO.markRoomAvailable(booking.roomId)

              val payload = Json.obj(
                "event" -> "CHECK_OUT",
                "guest" -> Json.obj(
                  "id" -> booking.guestId.toString
                )
              )

              println("Sending CHECK-OUT event: " + payload)

              producer.send(new ProducerRecord[String, String](topic, payload.toString()))
              Right(payload)
            }
          }
      }
    } yield result

}
