package models.json

import play.api.libs.json._
import models._
import java.sql.Timestamp

object ModelFormats {

  /** Custom formatter for Timestamp */
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    override def reads(json: JsValue): JsResult[Timestamp] =
      json.validate[String].map(str => Timestamp.valueOf(str))

    override def writes(ts: Timestamp): JsValue =
      JsString(ts.toString)
  }

  // Now your models will work
  implicit val visitorFormat: OFormat[Visitor] = Json.format[Visitor]
  implicit val departmentFormat: OFormat[Department] = Json.format[Department]
  implicit val employeeFormat: OFormat[Employee] = Json.format[Employee]
  implicit val visitLogFormat: OFormat[VisitLog] = Json.format[VisitLog]
}
