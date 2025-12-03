package pipelines

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object Pipeline2_Keyspaces_To_Parquet {

  def main(args: Array[String]): Unit = {

    println("========== PIPELINE 2 STARTED ==========")

    val config = ConfigFactory.load()
    val CASSANDRA_USERNAME = config.getString("cassandra.username")
    val CASSANDRA_PASSWORD = config.getString("cassandra.password")
    val TRUSTSTORE_PASSWORD = config.getString("truststore.password")
    val S3_ACCESS_KEY = config.getString("s3.accessKey")
    val S3_SECRET_KEY = config.getString("s3.secretKey")
    val S3_BUCKET_NAME = config.getString("s3.bucket")
    // -----------------------------
    // SPARK SESSION WITH KEYSPACES
    // -----------------------------
    val spark = SparkSession.builder()
      .appName("Pipeline2")
      .master("local[*]")
      .config("spark.cassandra.connection.host", "cassandra.us-east-1.amazonaws.com")
      .config("spark.cassandra.connection.port", "9142")
      .config("spark.cassandra.connection.ssl.enabled", "true")

      // KEYSPACES username/password
      .config("spark.cassandra.auth.username", CASSANDRA_USERNAME)
      .config("spark.cassandra.auth.password", CASSANDRA_PASSWORD)

      // Required for Amazon Keyspaces
      .config("spark.cassandra.input.consistency.level", "LOCAL_QUORUM")

      // TRUSTSTORE (same as pipeline 1)
      .config("spark.cassandra.connection.ssl.trustStore.path", "/Users/admin/cassandra_truststore.jks")
      .config("spark.cassandra.connection.ssl.trustStore.password", TRUSTSTORE_PASSWORD)

      // S3 Write Support
      .config("spark.hadoop.fs.s3a.access.key", S3_ACCESS_KEY)
      .config("spark.hadoop.fs.s3a.secret.key", S3_SECRET_KEY)
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")

      .getOrCreate()

    println(">>> Spark session created!")
    import spark.implicits._

    // -----------------------------
    // READ FROM AMAZON KEYSPACES
    // -----------------------------
    println(">>> Reading data from Keyspaces...")

    val df = spark.read
      .format("org.apache.spark.sql.cassandra")
      .option("keyspace", "retail")
      .option("table", "sales_data")
      .load()

    // -----------------------------
    // SELECT REQUIRED COLUMNS
    // -----------------------------
    val selected = df.select(
      "customer_id",
      "order_id",
      "amount",
      "product_name",
      "quantity"
    )

    println(">>> Selected columns:")
    selected.show(10, truncate = false)

    // -----------------------------
    // WRITE TO S3 AS PARQUET
    // -----------------------------
    println(">>> Writing to S3 in Parquet format...")
    val parquetPath = s"s3a://${S3_BUCKET_NAME}/retail-output/sales/parquet/"
    selected
      .write
      .mode("overwrite")
      .partitionBy("customer_id")
      .parquet(parquetPath)

    println(">>> Successfully written to S3!")

    println("========== PIPELINE 2 COMPLETED ==========")
  }
}
