package billing

import config.AppConfig
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object BillingJob {

  def run(spark: SparkSession, conf: AppConfig): Unit = {

    import spark.implicits._

    // =======================================
    // 0. Determine billing month dynamically
    // =======================================
    val billingMonth = java.time.LocalDate.now()   // previous month
    val billingMonthStr = billingMonth.toString.substring(0, 7)   // "yyyy-MM"

    val monthPrefix = billingMonthStr  // used to filter Avro folders

    println(s"Running Billing for Month: $billingMonthStr")

    // =======================================
    // 1. READ AVRO FROM S3
    // =======================================
    val avroDF = spark.read
      .format("avro")
      .option("recursiveFileLookup", "true")
      .load(conf.lakePath)

    // Ensure date column exists
    val avroWithDate =
      if (avroDF.columns.contains("date")) avroDF
      else avroDF.withColumn("date", to_date(col("event_time")))

    val filtered = avroWithDate
      .filter(col("date").startsWith(monthPrefix))
      .withColumn("billing_month", to_date(lit(billingMonthStr + "-31"))) // valid YYYY-MM-DD

    println(s"Loaded ${filtered.count()} Avro rows for billing month = $billingMonthStr")

    // =======================================
    // 2. READ MYSQL TABLES
    // =======================================
    val householdDF = MySQLReader.readTable(spark, "household", conf.mysqlUrl, conf.mysqlUser, conf.mysqlPass)
    val planDF      = MySQLReader.readTable(spark, "billing_plan", conf.mysqlUrl, conf.mysqlUser, conf.mysqlPass)
    val customerDF  = MySQLReader.readTable(spark, "customer", conf.mysqlUrl, conf.mysqlUser, conf.mysqlPass)

    // =======================================
    // 3. JOIN household → customer → plan
    // =======================================
    val joined = filtered
      .join(householdDF, "household_id")
      .join(customerDF, "customer_id")
      .join(planDF, "plan_id")

    // =======================================
    // 4. AGGREGATE MONTHLY CONSUMPTION
    // =======================================
    val aggregated = joined.groupBy(
      "household_id",
      "billing_month",
      "rate_per_liter",
      "fixed_charge",
      "tax_percent"
    ).agg(
      sum("consumption_liters").as("total_consumption_liters")
    )

    // =======================================
    // 5. BILLING CALCULATION
    // =======================================
    val finalBills = BillingCalculator.compute(aggregated)
      .coalesce(1)  // ensures safe JDBC write

    // =======================================
    // 6. WRITE TO MYSQL billing_history
    // =======================================
    MySQLWriter.write(finalBills, conf.mysqlUrl, conf.mysqlUser, conf.mysqlPass)

    println("✔ Billing Job Completed — Records inserted into billing_history")
  }
}
