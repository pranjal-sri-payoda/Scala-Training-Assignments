package com.spark.training

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise10 {
  def main(args: Array[String]): Unit = {
    println("===== EX10 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise10").master("local[*]").getOrCreate()
    import spark.implicits._

    val custCount = 2000000
    val txnCount = 5000000

    val custRDD = spark.sparkContext.parallelize(1 to custCount, 50).map { id =>
      val name = Random.alphanumeric.take(8).mkString
      (id, name)
    }
    val custDF = custRDD.toDF("customerId", "name")

    val txnRDD2 = spark.sparkContext.parallelize(1 to txnCount, 80).map { tid =>
      val cust = Random.nextInt(custCount) + 1
      val amt = Random.nextDouble() * 1000
      (tid, cust, amt)
    }
    val txnDF2 = txnRDD2.toDF("txnId", "customerId", "amount")

    println("Joining customers and transactions")
    val joined = custDF.join(txnDF2, Seq("customerId"))

    val totalSpend = joined.groupBy("customerId").agg(sum("amount").as("total_spend"))
    println("Sample totals:")
    totalSpend.show(5, truncate = false)

    val out = "output/ex10"
    totalSpend.write.mode("overwrite").parquet(s"$out/parquet")
    println("Done creating output changes")
    /*
      Observations:
      - Joins + groupBy create the largest shuffles because data must be redistributed across executors
        so that matching keys and grouped keys land on the same partition.
      - Parquet is best for analytical output because it's columnar, compressed, supports predicate pushdown,
        and preserves schema/types for faster downstream processing.
    */
    Thread.sleep(10000)
    spark.stop()
    println("===== EX10 DONE =====")
  }
}
