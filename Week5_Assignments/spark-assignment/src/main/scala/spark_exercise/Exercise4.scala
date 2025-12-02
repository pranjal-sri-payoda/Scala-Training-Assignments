package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise4 {
  def main(args: Array[String]): Unit = {
    println("===== EX4 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise4").master("local[*]").getOrCreate()
    import spark.implicits._

    val numProducts = 2000000
    val categories = Array("Electronics", "Clothes", "Books")

    val productRDD = spark.sparkContext.parallelize(1 to numProducts, 40).map { id =>
      val cat = categories(Random.nextInt(categories.length))
      val price = Random.nextDouble() * 2000
      val desc = Random.alphanumeric.take(50).mkString
      (id.toLong, cat, price, desc)
    }

    val productDF = productRDD.toDF("productId", "category", "price", "description")
    println("Generated products DF")

    // Filter price > 1000
    val filtered = productDF.filter($"price" > 1000)
    println("Filtered sample:")
    filtered.show(3, truncate = false)

    // Sort by price (will cause shuffle)
    val sorted = filtered.orderBy($"price".desc)
    println("Sorted sample:")
    sorted.show(5, truncate = false)

    // Write sorted data
    val out = "output/ex4"
    sorted.write.mode("overwrite").csv(s"$out/csv_sorted")
    sorted.write.mode("overwrite").parquet(s"$out/parquet_sorted")

    println("Wrote sorted CSV and Parquet")

    /*
      Observations:
      - Sorting forces a global shuffle (range partitioning) -> broad operation
      - CSV write is typically slower and larger due to lack of compression/columnar layout;
        Parquet is columnar and compresses better so faster for analytical reads/writes.
    */
    Thread.sleep(100000)
    spark.stop()
    println("===== EX4 DONE =====")
  }
}
