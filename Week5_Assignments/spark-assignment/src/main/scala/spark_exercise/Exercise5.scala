package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise5 {
  def main(args: Array[String]): Unit = {
    println("===== EX5 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise5").master("local[*]").getOrCreate()
    import spark.implicits._

    val numSensors = 3000000

    val sensorRDD = spark.sparkContext.parallelize(1 to numSensors, 40).map { _ =>
      val dev = "DEV_" + Random.nextInt(5000)
      val temp = 20 + Random.nextDouble() * 15
      val hum = 40 + Random.nextDouble() * 20
      val hour = Random.nextInt(24)
      (dev, temp, hum, hour)
    }

    val sensorDF = sensorRDD.toDF("deviceId", "temperature", "humidity", "hour")
    println("Generated sensor DF")

    // average temperature per hour
    val avgTemp = sensorDF.groupBy("hour").agg(avg("temperature").as("avg_temp"))
    avgTemp.show(24, truncate = false)

    // write to parquet partitioned by hour
    val out = "output/ex5"
    avgTemp.write.mode("overwrite").partitionBy("hour").parquet(s"$out/parquet_by_hour")

    println("Wrote partitioned parquet")

    /*
      Observations:
      - Partitioning by hour will create one folder per distinct hour value (0..23) so up to 24 folders.
      - groupBy(hour) is broad because it requires shuffling data so that same keys are colocated.
    */
    Thread.sleep(100000)
    spark.stop()
    println("===== EX5 DONE =====")
  }
}
