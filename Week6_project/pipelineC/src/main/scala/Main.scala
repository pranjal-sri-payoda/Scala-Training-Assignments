import billing.BillingJob
import config.AppConfig
import org.apache.spark.sql.SparkSession

object Main extends App {

  val conf = AppConfig.load()

  val spark = SparkSession.builder()
    .appName("Pipeline-C Billing Job")
    .master("local[*]")
    .config("spark.hadoop.fs.s3a.access.key", conf.accessKey)
    .config("spark.hadoop.fs.s3a.secret.key", conf.secretKey)
    .config("spark.hadoop.fs.s3a.aws.credentials.provider",
      "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
    .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .getOrCreate()

  BillingJob.run(spark, conf)

  spark.stop()
}
