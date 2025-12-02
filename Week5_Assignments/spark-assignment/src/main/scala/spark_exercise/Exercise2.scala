package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise2 {
  def main(args: Array[String]): Unit = {
    println("===== EX2 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise2").master("local[*]").getOrCreate()
    import spark.implicits._

    val numSales = 10000000
    val stores = (1 to 100).map(i => s"Store_$i").toArray

    val salesRDD = spark.sparkContext.parallelize(1 to numSales, 50).map { _ =>
      val store = stores(Random.nextInt(stores.length))
      val amt = Random.nextDouble() * 500
      (store, amt)
    }

    println("Generated sales RDD")

    // Compare groupByKey vs reduceByKey (measure times roughly)
    val t1 = System.nanoTime()
    val groupByKeyAgg = salesRDD.groupByKey().mapValues(_.sum) // expensive
    groupByKeyAgg.count() // force action
    val t2 = System.nanoTime()

    val gbkSec = (t2 - t1) / 1e9
    println(s"groupByKey time (s): $gbkSec")

    val t3 = System.nanoTime()
    val reduceByKeyAgg = salesRDD.reduceByKey(_ + _)
    reduceByKeyAgg.count()
    val t4 = System.nanoTime()

    val rdbkSec = (t4 - t3) / 1e9
    println(s"reduceByKey time (s): $rdbkSec")

    // Convert to DF and compute store-wise total using DF
    val salesDF = salesRDD.toDF("storeId", "amount")
    val dfAgg = salesDF.groupBy("storeId").agg(sum("amount").as("total_amount"))

    println("DF aggregation sample:")
    dfAgg.show(5, truncate = false)

    // Save result to Parquet
    val out = "output/ex2"
    dfAgg.write.mode("overwrite").parquet(s"$out/parquet")

    println("Wrote ex2 parquet to " + s"$out/parquet")

    /*
      Observations to note when you run:
      - groupByKey tends to be slower and more memory-heavy for 10M because it moves all values for a key
        and retains iterable collections per key (higher shuffle + memory).
      - reduceByKey performs partial aggregation locally (combiner) before shuffling -> less data movement.
      - groupByKey causes large shuffle; reduceByKey reduces shuffle size (narrow -> combiner -> broad).
    */
    Thread.sleep(300000)
    spark.stop()
    println("===== EX2 DONE =====")
  }
}

