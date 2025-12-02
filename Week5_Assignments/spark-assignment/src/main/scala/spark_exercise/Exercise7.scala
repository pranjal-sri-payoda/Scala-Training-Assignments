package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise7 {
  def main(args: Array[String]): Unit = {
    println("===== EX7 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise7").master("local[*]").getOrCreate()
    import spark.implicits._

    val numTxns = 3000000
    val txnRDD = spark.sparkContext.parallelize(1 to numTxns, 40).map { _ =>
      val acc = "ACC_" + Random.nextInt(100000)
      val amt = Random.nextDouble() * 10000
      (acc, amt)
    }

    println("Generated txn RDD")

    // RDD: reduceByKey -> sortBy
    val totalsRDD = txnRDD.reduceByKey(_ + _)
    val top10RDD = totalsRDD.sortBy(_._2, ascending = false).take(10)
    println("Top 10 accounts (RDD):")
    top10RDD.foreach(println)

    // DataFrame: groupBy -> sum -> orderBy
    val txnDF = txnRDD.toDF("accountId", "amount")
    val topDF = txnDF.groupBy("accountId").agg(sum("amount").as("total")).orderBy(desc("total")).limit(10)
    println("Top 10 accounts (DF):")
    topDF.show(10, truncate = false)

    val out = "output/ex7"
    topDF.write.mode("overwrite").json(s"$out/top10_json")

    /*
      Observations:
      - Sorting is a broad shuffle; grouping is broad as well.
      - DataFrame often performs faster due to Catalyst optimizations and native Tungsten execution.
    */
    Thread.sleep(10000)
    spark.stop()
    println("===== EX7 DONE =====")
  }
}
