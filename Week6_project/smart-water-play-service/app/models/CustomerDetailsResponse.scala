package models

import play.api.libs.json._

case class CustomerDetailsResponse(customer: Customer, household: Household)

object CustomerDetailsResponse {
  implicit val format: OFormat[CustomerDetailsResponse] = Json.format[CustomerDetailsResponse]
}
