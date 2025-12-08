package exercises

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object Exercise1Broadcasting {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise1Broadcasting")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // 1. Large transactions dataset
    val transactions = Seq(
      ("t1", 100.0, "EUR"),
      ("t2", 200.0, "INR"),
      ("t3", 50.0,  "USD"),
      ("t4", 300.0, "EUR")
    ).toDF("transactionId", "amount", "currencyCode")

    // 2. Small exchange rate dataset (this is what we'll broadcast)
    val exchangeRates = Seq(
      ("USD", 1.0),    // 1 USD = 1 USD
      ("EUR", 1.1),    // 1 EUR = 1.1 USD
      ("INR", 0.012)   // 1 INR = 0.012 USD
    ).toDF("currencyCode", "rateToUSD")

    // Identify the small dataset: exchangeRates is small enough to broadcast
    val ratesMap: Map[String, Double] =
      exchangeRates
        .collect()
        .map(row => row.getAs[String]("currencyCode") -> row.getAs[Double]("rateToUSD"))
        .toMap

    val ratesBC = spark.sparkContext.broadcast(ratesMap)  // read-only, shared with all executors

    // 3. UDF to convert to USD using broadcasted rates
    val toUSD = udf { (currency: String, amount: Double) =>
      val rate = ratesBC.value.getOrElse(currency, 1.0)  // default 1.0 if missing
      amount * rate
    }

    val transactionsInUSD = transactions
      .withColumn("amountUSD", toUSD(col("currencyCode"), col("amount")))

    // 4. Count number of transactions per currency (after conversion)
    val txCountPerCurrency = transactionsInUSD
      .groupBy("currencyCode")
      .count()

    println("Transactions with USD conversion:")
    transactionsInUSD.show(false)

    println("Transactions per currency:")
    txCountPerCurrency.show(false)

    spark.stop()
  }
}
