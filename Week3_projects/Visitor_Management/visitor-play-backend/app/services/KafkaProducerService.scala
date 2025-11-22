package services

import javax.inject._
import play.api.libs.json._

import models._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaProducerService @Inject()(
                                      visitorDAO: VisitorDAO,
                                      employeeDAO: EmployeeDAO,
                                      visitLogDAO: VisitLogDAO
                                    )(implicit ec: ExecutionContext) {

  // -------------------------------------------------
  // JSON Formats for producing JSON string payloads
  // -------------------------------------------------
  implicit val visitorFormat = Json.format[Visitor]
  implicit val employeeFormat = Json.format[Employee]
  implicit val timestampFormat: Format[java.sql.Timestamp] = new Format[java.sql.Timestamp] {
    def writes(ts: java.sql.Timestamp) = JsString(ts.toString)
    def reads(json: JsValue) = json.validate[String].map(java.sql.Timestamp.valueOf)
  }

  // -------------------------------------------------
  // Kafka Setup
  // -------------------------------------------------
  private val topic = "visitor_notifications"

  private val props = new java.util.Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  // -------------------------------------------------
  // SEND VISITOR CHECK-IN EVENT
  // -------------------------------------------------
  def processVisitorCheckIn(visitorId: Long): Future[Either[String, JsValue]] =
    for {
      visitorOpt <- visitorDAO.getById(visitorId)
      result <- visitorOpt match {

        case None =>
          Future.successful(Left("Visitor not found"))

        case Some(visitor) =>

          // FIX: extract hostEmployeeId from Option
          val hostId = visitor.hostEmployeeId.getOrElse(
            return Future.successful(Left("hostEmployeeId not provided"))
          )

          employeeDAO.getById(hostId).map {
            case None =>
              Left("Host employee not found")

            case Some(host) =>
              val payload = Json.obj(
                "event" -> "VISITOR_CHECK_IN",
                "visitor" -> Json.obj(
                  "id"        -> visitor.id,
                  "fullName"  -> visitor.fullName,
                  "email"     -> visitor.email,
                  "phone"     -> visitor.phone,
                  "purpose"   -> visitor.purpose,
                  "idProof"   -> visitor.idProof
                ),
                "host" -> Json.obj(
                  "id"   -> host.id,
                  "name" -> host.fullName,
                  "email" -> host.email,
                  "departmentId" -> host.departmentId
                )
              )

              producer.send(new ProducerRecord[String, String](topic, payload.toString()))
              Right(payload)
          }
      }
    } yield result

  // -------------------------------------------------
  // SEND VISITOR CHECK-OUT EVENT
  // -------------------------------------------------
  def processVisitorCheckOut(visitorId: Long): Future[Either[String, JsValue]] = {
    visitorDAO.getById(visitorId).flatMap {
      case None =>
        Future.successful(Left("Visitor not found"))

      case Some(visitor) =>
        visitLogDAO.getActiveVisitByVisitor(visitorId).flatMap {
          case None =>
            Future.successful(Left("No active visit found for this visitor"))

          case Some(activeVisit) =>
            visitLogDAO.markAsCheckedOut(activeVisit.id).map { _ =>
              val payload = Json.obj(
                "event" -> "VISITOR_CHECK_OUT",
                "visitor" -> Json.obj(
                  "id" -> visitor.id,
                  "fullName" -> visitor.fullName
                ),
                "visit" -> Json.obj(
                  "visitLogId" -> activeVisit.id,
                  "checkIn" -> activeVisit.checkIn,
                  "checkOut" -> java.sql.Timestamp.from(java.time.Instant.now())
                )
              )

              producer.send(
                new ProducerRecord[String, String](topic, payload.toString())
              )

              Right(payload)
            }
        }
    }
  }

}
