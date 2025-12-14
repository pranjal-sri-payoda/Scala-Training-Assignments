package streaming

import config.AppConfig
import model.ReadingSchema

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.streaming.Trigger

object StreamingProcessor {

  def start(conf: AppConfig): Unit = {

    val spark = SparkSession.builder()
      .appName("Pipeline-B")
      .master("local[*]")

      // S3 config
      .config("spark.hadoop.fs.s3a.access.key", conf.accessKey)
      .config("spark.hadoop.fs.s3a.secret.key", conf.secretKey)
      .config("spark.hadoop.fs.s3a.aws.credentials.provider",
        "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")

      // Spark 3.2.1 committer
      .config("spark.hadoop.fs.s3a.committer.name", "directory")
      .config("spark.hadoop.mapreduce.outputcommitter.factory.class",
        "org.apache.hadoop.fs.s3a.commit.S3ACommitterFactory")
      .config("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel("WARN")

    // -------------------------------------------
    // READ FROM KAFKA
    // -------------------------------------------
    val raw = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", conf.kafkaBootstrap)
      .option("subscribe", conf.topic)
      .option("startingOffsets", "latest")
      .load()

    val parsed = raw
      .selectExpr("CAST(value AS STRING) as json_str")
      .select(from_json($"json_str", ReadingSchema.schema).alias("data"))
      .select("data.*")
      .withColumn("ingestion_timestamp", current_timestamp())

    // -------------------------------------------
    // FOREACH BATCH
    // -------------------------------------------
    parsed.writeStream
      .foreachBatch { (batch: DataFrame, batchId: Long) =>
        val runId = java.util.UUID.randomUUID().toString
        processBatch(batch, batchId, conf, spark, runId)
      }
      .option("checkpointLocation", conf.checkpointPath)
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .start()
      .awaitTermination()
  }

  // =======================================================================
  // PROCESS BATCH
  // =======================================================================
  private def processBatch(
                            df: DataFrame,
                            batchId: Long,
                            conf: AppConfig,
                            spark: SparkSession,
                            runId: String
                          ): Unit = {

    import spark.implicits._

    if (df.rdd.isEmpty()) {
      println(s"[Batch $batchId] Empty → skipping")
      return
    }

    println(s"[Batch $batchId] Received ${df.count()} records")

    // ANOMALY STATS
    val stats = df.groupBy("meter_id")
      .agg(
        avg("consumption_liters").alias("mean"),
        stddev_pop("consumption_liters").alias("std")
      )

    val enriched = df.join(stats, "meter_id")
      .withColumn("is_spike", $"consumption_liters" > $"mean" + lit(3) * $"std")
      .withColumn("is_drop", $"consumption_liters" < $"mean" - lit(3) * $"std")
      .withColumn("event_time", ($"timestamp" / 1000).cast(TimestampType))
      .withColumn("date", to_date($"event_time"))
      .withColumn("hour", date_format($"event_time", "HH"))

    writeAvro(enriched, conf, runId)
    writeRecentJson(enriched, conf, spark)
  }

  // =======================================================================
  // FULL HISTORY → AVRO
  // =======================================================================
  private def writeAvro(df: DataFrame, conf: AppConfig, runId: String): Unit = {
    df.write
      .format("avro")
      .mode("append")
      .partitionBy("date", "hour")
      .save(s"${conf.lakePath}/run=$runId")
  }

  // =======================================================================
  // LAST 50 READINGS PER HOUSEHOLD → JSON
  // =======================================================================
  private def writeRecentJson(df: DataFrame, conf: AppConfig, spark: SparkSession): Unit = {

    import spark.implicits._

    val windowSpec = Window
      .partitionBy($"household_id")
      .orderBy($"event_time".desc)

    val limited = df
      .withColumn("rn", row_number().over(windowSpec))
      .filter($"rn" <= 50)
      .drop("rn")

    limited
      .write
      .mode("overwrite")
      .partitionBy("household_id")
      .json(conf.recentJsonPath)
  }
}
