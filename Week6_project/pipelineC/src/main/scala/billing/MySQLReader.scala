package billing

import org.apache.spark.sql.{DataFrame, SparkSession}

object MySQLReader {

  def readTable(spark: SparkSession, table: String, url: String, user: String, pass: String): DataFrame = {
    spark.read
      .format("jdbc")
      .option("url", url)
      .option("dbtable", table)
      .option("user", user)
      .option("password", pass)
      .load()
  }
}
