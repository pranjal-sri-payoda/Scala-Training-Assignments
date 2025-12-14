package reports

import config.AppConfig
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import java.time.{LocalDate, ZoneId}

object DailyReportsJob {

  def run(spark: SparkSession, conf: AppConfig): Unit = {

    import spark.implicits._

    // ===========================================
    // 0. Determine report date dynamically
    // ===========================================
    val reportDate = LocalDate.now(ZoneId.systemDefault())
      .minusDays(1)                     // ALWAYS yesterday
      .toString                         // "yyyy-MM-dd"

    println(s"=== Pipeline D: Daily Reports for date = $reportDate ===")

    // ===========================================
    // 1. READ AVRO FROM LAKE
    // ===========================================
    val avroDF = spark.read
      .format("avro")
      .option("recursiveFileLookup", "true")
      .load(conf.lakePath)

    val avroWithDate =
      if (avroDF.columns.contains("date")) avroDF
      else avroDF.withColumn("date", to_date(col("event_time")))

    val dayReadings = avroWithDate.filter(col("date") === lit(reportDate))

    val total = dayReadings.count()
    println(s"Loaded $total records for $reportDate")

    if (total == 0) {
      println(s"No data found for $reportDate – skipping.")
      return
    }

    // ===========================================
    // 2. Load Household Dimension
    // ===========================================
    val householdDF = MySQLReader.readTable(
      spark,
      table = "household",
      conf.mysqlUrl,
      conf.mysqlUser,
      conf.mysqlPass
    )

    // ===========================================
    // 3. JOIN
    // ===========================================
    val joined = dayReadings.join(householdDF, "household_id")

    val dimCols = Seq("date", "household_id", "district", "state")

    // ===========================================
    // 4A – daily consumption report
    // ===========================================
    val dailyConsumption = joined
      .groupBy(dimCols.map(col): _*)
      .agg(
        sum("consumption_liters").as("total_consumption"),
        avg("consumption_liters").as("avg_consumption"),
        max("consumption_liters").as("max_consumption")
      )

    writeAvro(dailyConsumption,
      s"${conf.reportPath}/daily_consumption_by_household")

    // ===========================================
    // 4B – pressure stats
    // ===========================================
    val dailyPressure = joined
      .groupBy(dimCols.map(col): _*)
      .agg(
        avg("pressure").as("avg_pressure"),
        min("pressure").as("min_pressure"),
        max("pressure").as("max_pressure"),
        stddev_pop("pressure").as("pressure_stddev")
      )

    writeAvro(dailyPressure,
      s"${conf.reportPath}/daily_pressure_stats")

    // ===========================================
    // 4C – anomaly counts
    // ===========================================
    val dailyAnomalies = joined
      .groupBy(dimCols.map(col): _*)
      .agg(
        sum(when(col("is_spike"), 1).otherwise(0)).as("spike_count"),
        sum(when(col("is_drop"), 1).otherwise(0)).as("drop_count"),
        count(lit(1)).as("total_events")
      )

    writeAvro(dailyAnomalies,
      s"${conf.reportPath}/daily_anomaly_counts")

    println("✔ Pipeline D completed successfully.")
  }

  // ------------------------------------------
  // Write Helper
  // ------------------------------------------
  private def writeAvro(df: DataFrame, path: String): Unit = {
    df.write
      .format("avro")
      .mode("append")
      .partitionBy("date")
      .save(path)
  }
}
