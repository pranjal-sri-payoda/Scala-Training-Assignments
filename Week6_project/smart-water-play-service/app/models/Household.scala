package models

import play.api.libs.json._

case class Household(
                      householdId: Long,
                      customerId: Option[Long],
                      address: Option[String],
                      district: Option[String],
                      state: Option[String],
                      pincode: Option[String],
                      meterId: Option[String],
                      createdAt: Option[String],
                      planId: Option[Long]
                    )

object Household {
  implicit val format: OFormat[Household] = Json.format[Household]
}
