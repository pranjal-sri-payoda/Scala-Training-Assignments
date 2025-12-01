import org.apache.spark.sql.{SparkSession, functions => F}

object TripDurationPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Trip Duration Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // Load cleaned parquet
    val df = spark.read
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/clean_trips_parquet")

    // Add only tripDurationMinutes column
    val dfFinal = df.withColumn(
      "tripDurationMinutes",
      (
        F.unix_timestamp(F.to_timestamp($"endTime", "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")) -
          F.unix_timestamp(F.to_timestamp($"startTime", "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
        ) / 60
    )

    dfFinal.show(20, truncate = false)
    dfFinal.printSchema()

    dfFinal.write
      .mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/trips_with_duration_parquet")

    spark.stop()
  }
}
