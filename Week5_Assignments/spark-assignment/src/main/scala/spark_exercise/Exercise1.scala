package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random

object Exercise1 {

  def main(args: Array[String]): Unit = {

    println("===== MAIN EXECUTED =====")

    val spark = SparkSession.builder()
      .appName("Exercise1")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    println("===== SPARK SESSION CREATED =====")

    // ---------------------------------------
    // EXAMPLE: CUSTOMER DATA GENERATION (5M)
    // ---------------------------------------

    val numRecords = 5000000
    val cities = (1 to 50).map(i => s"City_$i").toArray

    val customersRDD = spark.sparkContext
      .parallelize(1 to numRecords, numSlices = 50)
      .map { id =>
        val name = Random.alphanumeric.take(10).mkString
        val age = 18 + Random.nextInt(53)
        val city = cities(Random.nextInt(cities.length))
        (id.toLong, name, age, city)
      }

    val customersDF = customersRDD.toDF("customerId", "name", "age", "city")

    println("===== DATA GENERATED =====")

    // ---------------------------------------
    // QUESTION 1: Count using RDD reduceByKey
    // ---------------------------------------

    val cityCountRDD = customersRDD
      .map(x => (x._4, 1))
      .reduceByKey(_ + _)

    println("===== RDD COUNT DONE =====")
    cityCountRDD.take(5).foreach(println)

    // ---------------------------------------
    // QUESTION 1: Count using DataFrame
    // ---------------------------------------

    val cityCountDF = customersDF.groupBy("city").count()

    println("===== DF COUNT DONE =====")
    cityCountDF.show(5)

    // ---------------------------------------
    // WRITE OUTPUTS
    // ---------------------------------------

    val outputBase = "output/ex1"

    cityCountDF.write.mode("overwrite").csv(s"$outputBase/csv")
    cityCountDF.write.mode("overwrite").json(s"$outputBase/json")
    cityCountDF.write.mode("overwrite").parquet(s"$outputBase/parquet")

    println("===== OUTPUT WRITTEN =====")
    Thread.sleep(300000)
    spark.stop()
    println("===== SPARK STOPPED =====")
  }
}
