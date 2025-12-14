package config

import com.typesafe.config.Config

case class AppConfig(
                      kafkaBootstrap: String,
                      topic: String,
                      accessKey: String,
                      secretKey: String,
                      lakePath: String,
                      recentJsonPath: String,
                      checkpointPath: String
                    )

object AppConfig {
  def apply(conf: Config): AppConfig = AppConfig(
    kafkaBootstrap = conf.getString("streaming.kafka-bootstrap"),
    topic = conf.getString("streaming.topic"),
    accessKey = conf.getString("s3.accessKey"),
    secretKey = conf.getString("s3.secretKey"),
    lakePath = conf.getString("s3.lake-path"),
    recentJsonPath = conf.getString("s3.recent-json-path"),
    checkpointPath = conf.getString("s3.checkpoint")
  )
}
