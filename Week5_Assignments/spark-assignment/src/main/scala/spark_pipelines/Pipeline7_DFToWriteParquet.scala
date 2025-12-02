package spark_pipelines

import org.apache.spark.sql.SparkSession

object Pipeline7_DFToWriteParquet {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Write Parquet Pipeline")
      .master("local[*]")
      .getOrCreate()

    // Load previous pipeline output
    val df2 = spark.read.parquet(
      "/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/trips_with_duration_parquet"
    )

    // Save DF to Parquet
    df2.write
      .mode("overwrite")
      .parquet("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/parquet/trips_clean.parquet")

    println("Parquet write completed!")
    spark.stop()
  }
}
