package billing

import org.apache.spark.sql.{DataFrame, SaveMode}

object MySQLWriter {
  def write(df: DataFrame, url: String, user: String, pass: String): Unit = {
    df.write
      .format("jdbc")
      .option("url", url)
      .option("dbtable", "billing_history")
      .option("user", user)
      .option("password", pass)
      .mode(SaveMode.Append)
      .save()
  }
}
