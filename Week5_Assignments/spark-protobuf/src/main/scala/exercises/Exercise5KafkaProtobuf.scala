package exercises

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.from_protobuf

object Exercise5KafkaProtobuf {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("UserEventConsumer")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val kafkaBootstrap = "localhost:9092"
    val topic = "user-events"

    // 1. Read from Kafka (value is byte[] → correct for Protobuf)
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrap)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    // Keep only binary payload
    val valueDF = kafkaDF.select(col("value").as("payload"))

    // 2. Deserialize protobuf
//    val descUri =
//      this.getClass.getClassLoader
//        .getResource("UserEvent.desc")
//        .toURI
//        .toString
    val descUri = Option(Exercise5KafkaProtobuf.getClass.getResource("/UserEvent.desc"))
      .map(_.toString)
      .getOrElse(throw new RuntimeException("Descriptor file not found in classpath!"))

    val decodedDF = valueDF.select(
      from_protobuf(
        col("payload"),
        "events.UserEvent",
        descUri
      ).as("ue")
    )


    // 3. Filter out malformed protobuf messages
    val validEventsDF = decodedDF
      .where(col("ue").isNotNull)
      .select("ue.userId", "ue.action", "ue.value")

    // 4. Count events per action
    val eventsPerAction = validEventsDF
      .groupBy("action")
      .count()

    // 5. Top 5 users with highest cumulative value
    val topUsers = validEventsDF
      .groupBy("userId")
      .agg(sum("value").as("totalValue"))
      .orderBy(desc("totalValue"))
      .limit(5)

    // 6. Output both to console
    val q1 = eventsPerAction.writeStream
      .outputMode("complete")
      .format("console")
      .option("truncate", false)
      .queryName("events_per_action")
      .start()

    val q2 = topUsers.writeStream
      .outputMode("complete")
      .format("console")
      .option("truncate", false)
      .queryName("top_users")
      .start()

    spark.streams.awaitAnyTermination()
  }
}
