package exercises

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object Exercise4Coalesce {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise4Coalesce")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // Dummy logs: imagine we had many partitions originally
    val logs = spark.range(0, 1000000)
      .withColumnRenamed("id", "logId")
      .repartition(100) // simulate many partitions

    // Filter a lot of records out
    val filteredLogs = logs.filter(col("logId") % 1000 === 0)

    println(s"Partitions before coalesce: ${filteredLogs.rdd.getNumPartitions}")

    // 1. Reduce number of partitions using coalesce (no full shuffle)
    val reduced = filteredLogs.coalesce(10)

    println(s"Partitions after coalesce: ${reduced.rdd.getNumPartitions}")

    // 2. Write to disk – you will get ~10 output files instead of 100
    reduced.write
      .mode("overwrite")
      .parquet("output/logs_filtered_coalesced")

    spark.stop()
  }
}

