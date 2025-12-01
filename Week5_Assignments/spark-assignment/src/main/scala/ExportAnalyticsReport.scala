import org.apache.spark.sql.{SparkSession, functions => F}

object ExportAnalyticsReport {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Export Analytics Report")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // Load the DataFrame with duration
    val df = spark.read
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/trips_with_duration_parquet")

    // --- Pipeline 10 Aggregation ---
    // Revenue per vehicleType per day
    val dailyRevenue = df
      .withColumn("tripDate", F.to_date($"startTime"))
      .groupBy("vehicleType", "tripDate")
      .agg(
        F.round(F.sum("fareAmount"), 2).alias("totalRevenue"),
        F.count("*").alias("tripCount")
      )
      .orderBy("tripDate", "vehicleType")

    // Convert output to a pretty string for text export
    val formatted = dailyRevenue.map { row =>
      val vt = row.getAs[String]("vehicleType")
      val date = row.getAs[java.sql.Date]("tripDate")
      val revenue = row.getAs[Double]("totalRevenue")
      val count = row.getAs[Long]("tripCount")

      s"Date: $date | Vehicle: $vt | Trips: $count | Revenue: $$ $revenue"
    }

    // Save as text file (one output file)
    formatted
      .coalesce(1)
      .write
      .mode("overwrite")
      .text("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/reports/vehicle_daily_revenue")

    spark.stop()
  }
}
