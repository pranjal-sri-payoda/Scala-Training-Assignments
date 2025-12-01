import org.apache.spark.sql.{SparkSession, functions => F}

object CleanTripDataFrame {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Clean DataFrame")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // Load DF from previous pipeline (reuse code or load saved file)
    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/urbanmove_trips.csv")

    // Convert startTime and endTime to timestamp
    val dfWithTime = df
      .withColumn("startTimeTS", F.to_timestamp($"startTime"))
      .withColumn("endTimeTS", F.to_timestamp($"endTime"))

    // Clean DataFrame
    val cleanedDF = dfWithTime
      .filter($"distanceKm" > 0)                // keep valid distance
      .filter($"fareAmount" >= 0)               // keep valid fares
      .filter($"startTimeTS".isNotNull)         // valid start time
      .filter($"endTimeTS".isNotNull)           // valid end time
      .filter($"startTimeTS" < $"endTimeTS")    // ensure start < end
      .drop("startTimeTS", "endTimeTS")         // remove helper columns

    cleanedDF.show(20, truncate = false)
    cleanedDF.printSchema()

    // Save cleaned DF (optional)
    cleanedDF.write
      .mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/clean_trips_parquet")

    spark.stop()
  }
}
