package pipelines

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Pipeline3_Parquet_To_JSON {

  def main(args: Array[String]): Unit = {

    println("========== PIPELINE 3 STARTED ==========")
    val config = ConfigFactory.load()
    val S3_ACCESS_KEY = config.getString("s3.accessKey")
    val S3_SECRET_KEY = config.getString("s3.secretKey")
    val S3_BUCKET_NAME = config.getString("s3.bucket")
    // -----------------------
    // SPARK SESSION
    // -----------------------
    val spark = SparkSession.builder()
      .appName("Pipeline3")
      .master("local[*]")
      .config("spark.hadoop.fs.s3a.access.key", S3_ACCESS_KEY)
      .config("spark.hadoop.fs.s3a.secret.key", S3_SECRET_KEY)
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")
      .getOrCreate()

    println(">>> Spark Session created successfully!")

    // -----------------------
    // READ PARQUET FROM S3
    // -----------------------
    println(">>> Reading parquet from S3...")

    val inputPath = s"s3a://${S3_BUCKET_NAME}/retail-output/sales/parquet/"

    val df = spark.read
      .parquet(inputPath)

    println(">>> Read completed. Total rows = " + df.count())
    df.show(10, truncate = false)

    // -----------------------
    // AGGREGATION
    // -----------------------
    println(">>> Performing aggregations for each product...")

    val result = df.groupBy("product_name")
      .agg(
        sum("quantity").alias("total_quantity"),
        sum("amount").alias("total_revenue")
      )

    println(">>> Aggregation complete. Sample output:")
    result.show(10, truncate = false)

    // -----------------------
    // WRITE JSON TO S3
    // -----------------------
    val outputPath = s"s3a://${S3_BUCKET_NAME}/retail-output/aggregates/products.json"

    println(s">>> Writing aggregated JSON to: $outputPath")

    result.write
      .mode("overwrite")
      .json(outputPath)

    println(">>> Successfully written aggregated results to S3!")
    println("========== PIPELINE 3 COMPLETED ==========")

    spark.stop()
  }
}
