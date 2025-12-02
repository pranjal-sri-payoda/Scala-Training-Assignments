package spark_pipelines

import org.apache.spark.sql.SparkSession

object Pipeline6_TripSQL {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Trip SQL Pipeline")
      .master("local[*]")
      .getOrCreate()

    // Load DataFrame
    val df = spark.read.parquet(
      "/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/trips_with_duration_parquet"
    )

    // Register DF as SQL temp table
    df.createOrReplaceTempView("trips")

    // --------------------------------------------------------------------
    // 1️⃣ Count trips per vehicleType
    // --------------------------------------------------------------------
    val sql1 =
      spark.sql("""
        SELECT vehicleType, COUNT(*) AS tripCount
        FROM trips
        GROUP BY vehicleType
        ORDER BY tripCount DESC
      """)

    println("\n===== Trips per Vehicle Type =====")
    sql1.show(false)

    // --------------------------------------------------------------------
    // 2️⃣ Average fare by route (startLocation → endLocation)
    // --------------------------------------------------------------------
    val sql2 =
      spark.sql("""
        SELECT startLocation, endLocation,
               AVG(fareAmount) AS avgFare
        FROM trips
        GROUP BY startLocation, endLocation
        ORDER BY avgFare DESC
      """)

    println("\n===== Average Fare by Route =====")
    sql2.show(false)

    // --------------------------------------------------------------------
    // 3️⃣ Count trips per payment method
    // --------------------------------------------------------------------
    val sql3 =
      spark.sql("""
        SELECT paymentMethod, COUNT(*) AS paymentCount
        FROM trips
        GROUP BY paymentMethod
        ORDER BY paymentCount DESC
      """)

    println("\n===== Trips per Payment Method =====")
    sql3.show(false)

    // --------------------------------------------------------------------
    // Save results (optional)
    // --------------------------------------------------------------------
    sql1.write.mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/sql_pipeline/sql_vehicleType_count")

    sql2.write.mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/sql_pipeline/sql_avg_fare_by_route")

    sql3.write.mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/sql_pipeline/sql_payment_method_count")

    spark.stop()
  }
}
