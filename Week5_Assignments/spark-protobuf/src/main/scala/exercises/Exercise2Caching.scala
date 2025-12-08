package exercises

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object Exercise2Caching {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise2Caching")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val sales = Seq(
      ("c1", "p1", 2, 100.0),
      ("c1", "p2", 1, 50.0),
      ("c2", "p1", 3, 150.0),
      ("c3", "p3", 5, 500.0)
    ).toDF("customerId", "productId", "quantity", "amount")

    // Helper function to measure time
    def time[R](label: String)(block: => R): R = {
      val t1 = System.nanoTime()
      val result = block
      val t2 = System.nanoTime()
      println(s"$label took ${(t2 - t1) / 1e9} seconds")
      result
    }

    // 1) Without caching
    println("=== Without caching ===")

    time("Total amount per customer") {
      val totalAmountPerCustomer = sales
        .groupBy("customerId")
        .agg(sum("amount").as("totalAmount"))

      totalAmountPerCustomer.show(false)
    }

    time("Total quantity per product") {
      val totalQtyPerProduct = sales
        .groupBy("productId")
        .agg(sum("quantity").as("totalQuantity"))

      totalQtyPerProduct.show(false)
    }

    // 2) With caching
    println("=== With caching ===")

    val cachedSales = sales.cache() // or .persist(StorageLevel.MEMORY_AND_DISK)

    // First action to materialize the cache
    cachedSales.count()

    time("Total amount per customer (cached)") {
      val totalAmountPerCustomer = cachedSales
        .groupBy("customerId")
        .agg(sum("amount").as("totalAmount"))

      totalAmountPerCustomer.show(false)
    }

    time("Total quantity per product (cached)") {
      val totalQtyPerProduct = cachedSales
        .groupBy("productId")
        .agg(sum("quantity").as("totalQuantity"))

      totalQtyPerProduct.show(false)
    }

    spark.stop()
  }
}
