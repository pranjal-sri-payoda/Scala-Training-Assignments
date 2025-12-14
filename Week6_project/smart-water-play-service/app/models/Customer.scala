package models

import play.api.libs.json._

case class Customer(
                     customerId: Long,
                     name: Option[String],
                     email: Option[String],
                     phone: Option[String],
                     createdAt: Option[String]
                   )

object Customer {
  implicit val format: OFormat[Customer] = Json.format[Customer]
}
