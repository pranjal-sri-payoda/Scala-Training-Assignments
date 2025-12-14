package billing

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object BillingCalculator {

  def compute(df: DataFrame): DataFrame = {

    df.withColumn(
      "sub_total",
      col("total_consumption_liters") * col("rate_per_liter") + col("fixed_charge")
    ).withColumn(
      "total_amount",
      col("sub_total") + (col("sub_total") * (col("tax_percent") / 100))
    ).select(
      "household_id",
      "billing_month",
      "total_consumption_liters",
      "total_amount"
    )
  }
}
