package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models._

@Singleton
class CustomerService @Inject()(
                                 dbConfigProvider: DatabaseConfigProvider
                               )(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class CustomersTable(tag: Tag) extends Table[Customer](tag, "customer") {
    def id = column[Long]("customer_id", O.PrimaryKey)
    def name = column[Option[String]]("name")
    def email = column[Option[String]]("email")
    def phone = column[Option[String]]("phone")
    def createdAt = column[Option[java.sql.Timestamp]]("created_at")

    def * =
      (id, name, email, phone, createdAt) <> (
        { case (id, nm, em, ph, ts) =>
          Customer(id, nm, em, ph, ts.map(_.toString))
        },
        { c: Customer =>
          Some((c.customerId, c.name, c.email, c.phone, c.createdAt.map(java.sql.Timestamp.valueOf)))
        }
      )
  }

  private class HouseholdsTable(tag: Tag) extends Table[Household](tag, "household") {
    def id = column[Long]("household_id", O.PrimaryKey)
    def customerId = column[Option[Long]]("customer_id")
    def address = column[Option[String]]("address")
    def district = column[Option[String]]("district")
    def state = column[Option[String]]("state")
    def pincode = column[Option[String]]("pincode")
    def meterId = column[Option[String]]("meter_id")
    def createdAt = column[Option[java.sql.Timestamp]]("created_at")
    def planId = column[Option[Long]]("plan_id")

    def * =
      (id, customerId, address, district, state, pincode, meterId, createdAt, planId) <> (
        { case (id, cid, addr, dis, st, pin, meter, ts, pid) =>
          Household(id, cid, addr, dis, st, pin, meter, ts.map(_.toString), pid)
        },
        { h: Household =>
          Some(
            (
              h.householdId, h.customerId, h.address, h.district, h.state,
              h.pincode, h.meterId, h.createdAt.map(java.sql.Timestamp.valueOf), h.planId
            )
          )
        }
      )
  }

  private val customers = TableQuery[CustomersTable]
  private val households = TableQuery[HouseholdsTable]

  def getCustomerDetails(customerId: Long): Future[Option[CustomerDetailsResponse]] = {
    val q = for {
      (c, h) <- customers join households on (_.id === _.customerId)
      if c.id === customerId
    } yield (c, h)

    db.run(q.result.headOption).map(_.map { case (c, h) => CustomerDetailsResponse(c, h) })
  }
}
