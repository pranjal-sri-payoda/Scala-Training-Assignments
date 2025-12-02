package com.spark.training

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise9 {
  def main(args: Array[String]): Unit = {
    println("===== EX9 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise9").master("local[*]").getOrCreate()
    import spark.implicits._

    val numStudents = 1500000
    val studentRDD = spark.sparkContext.parallelize(1 to numStudents, 20).map { id =>
      val name = Random.alphanumeric.take(6).mkString
      val score = Random.nextInt(100)
      (id, name, score)
    }

    val studentDF = studentRDD.toDF("studentId", "name", "score")
    println("Generated student DF")

    val sorted = studentDF.orderBy(desc("score"))
    sorted.show(5, truncate = false)

    val out = "output/ex9"
    sorted.write.mode("overwrite").json(s"$out/sorted_json")
    println("Done writing in output folder")
    /*
      Observations:
      - Sorting is a broad operation causing shuffle.
      - JSON writes are typically slower and produce larger files compared to columnar formats like Parquet.
    */
    Thread.sleep(10000)
    spark.stop()
    println("===== EX9 DONE =====")
  }
}