import org.apache.spark.sql.SparkSession

object ParquetReAggPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Re-Aggregation From Parquet")
      .master("local[*]")
      .getOrCreate()

    // Read from previously saved parquet
    val pq = spark.read.parquet(
      "/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/parquet/trips_clean.parquet"
    )

    // Re-aggregate from Parquet (optimized read)
    println("=== Average fareAmount by vehicleType ===")
    pq.groupBy("vehicleType")
      .avg("fareAmount")
      .show()

    println("=== Trip count by paymentMethod ===")
    pq.groupBy("paymentMethod")
      .count()
      .show()

    println("=== Max distance by startLocation ===")
    pq.groupBy("startLocation")
      .max("distanceKm")
      .show()

    spark.stop()
  }
}
