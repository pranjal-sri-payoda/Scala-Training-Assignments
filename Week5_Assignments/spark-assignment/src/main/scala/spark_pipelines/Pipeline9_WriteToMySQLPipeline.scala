package spark_pipelines

import org.apache.spark.sql.SparkSession

object Pipeline9_WriteToMySQLPipeline {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Write To MySQL")
      .master("local[*]")
      .getOrCreate()

    // Load df2 from previous pipeline
    val df2 = spark.read
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/trips_with_duration_parquet")

    // Write to MySQL
    df2.limit(10)
      .write
      .format("jdbc")
      .option("url", "jdbc:mysql://scaladb.mysql.com/db_name")
      .option("dbtable", "trip_summary")
      .option("user", "mysqladmin")
      .option("password", "password")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .mode("append")
      .save()


    spark.stop()
  }
}
