package models

import play.api.libs.json._

case class MeterReading(
                         meterId: Option[String],
                         householdId: Option[Long],
                         consumptionLiters: Option[Double],
                         pressure: Option[Double],
                         deviceStatus: Option[String],
                         timestamp: Option[Long],
                         ingestionTimestamp: Option[String],
                         eventTime: Option[String],
                         date: Option[String],
                         hour: Option[String],
                         isSpike: Option[Boolean],
                         isDrop: Option[Boolean],
                         mean: Option[Double],
                         std: Option[Double]
                       )

case class DailyUsage(
                       date: String,
                       household_id: Long,
                       district: Option[String],
                       state: Option[String],
                       total_consumption: Option[Double],
                       avg_consumption: Option[Double],
                       max_consumption: Option[Double]
                     )

case class BillingRecord(
                          billId: Long,
                          householdId: Option[Long],
                          billingMonth: String,           // "YYYY-MM-DD"
                          totalConsumptionLiters: Option[Double],
                          totalAmount: Option[Double],
                          generatedAt: Option[String]
                        )

object HouseholdModels {
  implicit val fmtReading = Json.format[MeterReading]
  implicit val fmtDailyUsage = Json.format[DailyUsage]
  implicit val fmtBilling = Json.format[BillingRecord]
}
