import org.apache.spark.sql.{SparkSession, functions => F}

object TripAggregations {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Trip Aggregation Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val df = spark.read.parquet(
      "/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/trips_with_duration_parquet"
    )

    // 📌 1️⃣ Average trip distance by vehicleType
    val avgDistanceByVehicle = df
      .groupBy($"vehicleType")
      .agg(F.avg($"distanceKm").alias("averageDistanceKm"))
      .orderBy($"averageDistanceKm".desc)

    // 📌 2️⃣ Revenue per day
    val revenueByDay = df
      .withColumn("tripDate", F.to_date($"startTime"))
      .groupBy($"tripDate")
      .agg(F.sum($"fareAmount").alias("totalRevenue"))
      .orderBy($"tripDate")

    // 📌 3️⃣ Top 5 most used routes
    val topRoutes = df
      .groupBy($"startLocation", $"endLocation")
      .agg(F.count("*").alias("tripCount"))
      .orderBy($"tripCount".desc)
      .limit(5)

    // Show output
    println("\n===== Average Distance by Vehicle Type =====")
    avgDistanceByVehicle.show(false)

    println("\n===== Revenue Per Day =====")
    revenueByDay.show(false)

    println("\n===== Top 5 Most Used Routes =====")
    topRoutes.show(false)

    // Save results
    avgDistanceByVehicle.write.mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/aggregate_pipeline/agg_avg_distance_by_vehicle")

    revenueByDay.write.mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/aggregate_pipeline/agg_revenue_by_day")

    topRoutes.write.mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/aggregate_pipeline/agg_top_routes")

    spark.stop()
  }
}
