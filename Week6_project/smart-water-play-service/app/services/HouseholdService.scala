package services

import models.DailyUsage
import javax.inject._
import play.api.Configuration
import scala.concurrent.{ExecutionContext, Future}
import utils.{AvroS3Reader, S3JsonReader}

@Singleton
class HouseholdService @Inject()(
                                  config: Configuration,
                                  s3JsonReader: S3JsonReader,
                                  avroReader: AvroS3Reader
                                )(implicit ec: ExecutionContext) {

  def getRecentReadings(householdId: Long, limit: Int) = {
    val path = s"${config.get[String]("s3.recentReadingsPath")}/household_id=$householdId/"
    s3JsonReader.readRecentReadings(path, limit)
  }

  def getDailyUsage(householdId: Long): Future[DailyUsage] = Future {

    val today = java.time.LocalDate.now().toString
    val base = config.get[String]("s3.reportsBasePath")
    val path = s"$base/daily_consumption_by_household/date=$today/"

    val records = avroReader.readAvroFolder(path)

    // JSON field inside AVRO = "household_id"
    val found = records.find(js => (js \ "household_id").as[Long] == householdId)

    found match {
      case Some(js) =>
        DailyUsage(
          date = today,
          household_id = householdId,
          district = (js \ "district").asOpt[String],
          state = (js \ "state").asOpt[String],
          total_consumption = (js \ "total_consumption").asOpt[Double],
          avg_consumption = (js \ "avg_consumption").asOpt[Double],
          max_consumption = (js \ "max_consumption").asOpt[Double]
        )

      case None =>
        DailyUsage(
          date = today,
          household_id = householdId,
          district = None,
          state = None,
          total_consumption = None,
          avg_consumption = None,
          max_consumption = None
        )
    }
  }
}
