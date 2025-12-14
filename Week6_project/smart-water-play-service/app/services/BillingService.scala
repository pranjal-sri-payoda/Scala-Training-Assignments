package services

import models.BillingRecord
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.{Date, Timestamp}

@Singleton
class BillingService @Inject()(
                                dbConfigProvider: DatabaseConfigProvider
                              )(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class BillingTable(tag: Tag) extends Table[BillingRecord](tag, "billing_history") {
    def billId = column[Long]("bill_id", O.PrimaryKey, O.AutoInc)
    def householdId = column[Option[Long]]("household_id")
    def billingMonth = column[Date]("billing_month")
    def totalConsumption = column[Option[Double]]("total_consumption_liters")
    def totalAmount = column[Option[Double]]("total_amount")
    def generatedAt = column[Option[Timestamp]]("generated_at")

    def * =
      (billId, householdId, billingMonth, totalConsumption, totalAmount, generatedAt) <> (
        { case (id, hid, date, cons, amt, gen) =>
          BillingRecord(id, hid, date.toString, cons, amt, gen.map(_.toString))
        },
        { record: BillingRecord =>
          Some(
            (
              record.billId,
              record.householdId,
              Date.valueOf(record.billingMonth),
              record.totalConsumptionLiters,
              record.totalAmount,
              record.generatedAt.map(Timestamp.valueOf)
            )
          )
        }
      )
  }

  private val bills = TableQuery[BillingTable]

  def getBills(householdId: Long): Future[Seq[BillingRecord]] = {
    val q = bills.filter(_.householdId === householdId).sortBy(_.billingMonth.desc)
    db.run(q.result)
  }
}
