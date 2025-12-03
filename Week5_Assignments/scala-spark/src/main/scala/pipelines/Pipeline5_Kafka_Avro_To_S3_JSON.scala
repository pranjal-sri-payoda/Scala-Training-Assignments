package pipelines

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.avro._

object Pipeline5_Kafka_Avro_To_S3_JSON {

  def main(args: Array[String]): Unit = {

    println("========== PIPELINE 5 :: Kafka Avro → JSON → S3 STARTED ==========")

    // --------------------------------------------------------
    // 1. LOAD CONFIG
    // --------------------------------------------------------
    val config = ConfigFactory.load()

    val S3_ACCESS_KEY          = config.getString("s3.accessKey")
    val S3_SECRET_KEY          = config.getString("s3.secretKey")
    val S3_BUCKET_NAME         = config.getString("s3.bucket")
    val KAFKA_BOOTSTRAP_SERVER = config.getString("kafka.bootstrapServer")
    val KAFKA_TOPIC            = config.getString("kafka.topic")

    println(s">>> Loaded Config:")
    println(s"    - Kafka Topic         : $KAFKA_TOPIC")
    println(s"    - Kafka Bootstrap     : $KAFKA_BOOTSTRAP_SERVER")
    println(s"    - S3 Bucket           : $S3_BUCKET_NAME")
    println("--------------------------------------------------------")

    // --------------------------------------------------------
    // 2. CREATE SPARK SESSION
    // --------------------------------------------------------
    val spark = SparkSession.builder()
      .appName("Pipeline5_Kafka_Avro_To_S3_JSON")
      .master("local[*]")
      .config("spark.sql.streaming.schemaInference", "true")

      // Required for S3 output
      .config("spark.hadoop.fs.s3a.access.key", S3_ACCESS_KEY)
      .config("spark.hadoop.fs.s3a.secret.key", S3_SECRET_KEY)
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.path.style.access", "true")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")

      .getOrCreate()

    println(">>> SparkSession created successfully.")
    println("--------------------------------------------------------")

    import spark.implicits._

    // --------------------------------------------------------
    // 3. READ FROM KAFKA
    // --------------------------------------------------------
    println(s">>> Subscribing to Kafka topic: $KAFKA_TOPIC")
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVER)
      .option("subscribe", KAFKA_TOPIC)
      .option("startingOffsets", "latest")
      .load()

    println(">>> Kafka Stream initialized successfully.")
    println("--------------------------------------------------------")

    // --------------------------------------------------------
    // 4. AVRO SCHEMA
    // --------------------------------------------------------
    val avroSchema =
      """
        {
          "type": "record",
          "name": "OrderRecord",
          "namespace": "com.retail",
          "fields": [
            { "name": "order_id", "type": "int" },
            { "name": "customer_id", "type": "int" },
            { "name": "amount", "type": "double" },
            { "name": "created_at", "type": "string" }
          ]
        }
      """

    println(">>> Avro schema loaded successfully.")
    println("--------------------------------------------------------")

    // --------------------------------------------------------
    // 5. DECODE AVRO → STRUCT
    // --------------------------------------------------------
    println(">>> Decoding Avro payloads from Kafka...")
    val decodedDF = kafkaDF
      .select(from_avro(col("value"), avroSchema).as("data"))
      .select("data.*")

    println(">>> Avro decoded successfully. Schema:")
    decodedDF.printSchema()
    println("--------------------------------------------------------")

    // --------------------------------------------------------
    // 6. WRITE JSON TO S3 (Streaming)
    // --------------------------------------------------------
    val outputPath = s"s3a://${S3_BUCKET_NAME}/retail-output/stream/json/"
    val checkpointPath = s"s3a://${S3_BUCKET_NAME}/retail-output/checkpoints/pipeline5/"

    println(s">>> Writing Kafka Stream to S3 in JSON format...")
    println(s"    - Output Path     : $outputPath")
    println(s"    - Checkpoint Path : $checkpointPath")

    val query = decodedDF.writeStream
      .format("json")
      .option("path", outputPath)
      .option("checkpointLocation", checkpointPath)
      .outputMode("append")
      .start()

    println(">>> Pipeline 5 is RUNNING. Waiting for new Kafka messages...")
    println(">>> Streaming JSON to S3 as events arrive.")
    println("============================================================")

    query.awaitTermination()

    println("========== PIPELINE 5 COMPLETED ==========")
  }
}
