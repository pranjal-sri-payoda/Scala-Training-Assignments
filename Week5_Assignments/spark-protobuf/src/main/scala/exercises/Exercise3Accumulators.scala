package exercises

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object Exercise3Accumulators {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise3Accumulators")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val threshold = 500.0

    val transactions = Seq(
      (1, 100.0),
      (2, 600.0),
      (3, 800.0),
      (4, 400.0),
      (5, 1000.0)
    ).toDF("transactionId", "amount")

    // 1. Create accumulator on the driver
    val highValueCount = spark.sparkContext.longAccumulator("highValueTxCount")

    // 2. Use accumulator inside an action
    transactions.foreach { row =>
      val amt = row.getAs[Double]("amount")
      if (amt > threshold) {
        highValueCount.add(1L)
      }
    }

    // 3. Read accumulator value on driver
    println(s"Number of transactions > $threshold = ${highValueCount.value}")

    spark.stop()
  }
}

