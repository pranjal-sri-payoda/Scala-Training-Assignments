import com.typesafe.config.ConfigFactory
import config.AppConfig
import streaming.StreamingProcessor

object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val appConfig = AppConfig(config)

    println(s"\n=== Starting Spark Streaming Processor ===")
    println(s"Kafka Topic      : ${appConfig.topic}")
    println(s"Lake Output Path : ${appConfig.lakePath}")
    println(s"Recent JSON Path : ${appConfig.recentJsonPath}\n")

    StreamingProcessor.start(appConfig)
  }
}
