package pipelines

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object Pipeline1_MySQL_To_Keyspaces {

  def main(args: Array[String]): Unit = {

    println("========== PIPELINE 1 STARTED ==========")

    val config = ConfigFactory.load()
    val CASSANDRA_USERNAME = config.getString("cassandra.username")
    val CASSANDRA_PASSWORD = config.getString("cassandra.password")
    val TRUSTSTORE_PASSWORD = config.getString("truststore.password")
    val MYSQL_HOST_URL = config.getString("mysql.url")
    val MYSQL_USERNAME = config.getString("mysql.username")
    val MYSQL_PASSWORD = config.getString("mysql.password")

    val spark = SparkSession.builder()
      .appName("Pipeline1")
      .master("local[*]")
      .config("spark.cassandra.connection.host", "cassandra.us-east-1.amazonaws.com")
      .config("spark.cassandra.connection.port", "9142")
      .config("spark.cassandra.connection.ssl.enabled", "true")
      .config("spark.cassandra.auth.username", CASSANDRA_USERNAME)
      .config("spark.cassandra.auth.password", CASSANDRA_PASSWORD)
      .config("spark.cassandra.input.consistency.level", "LOCAL_QUORUM")
      .config("spark.cassandra.connection.ssl.trustStore.path", "/Users/admin/cassandra_truststore.jks")
      .config("spark.cassandra.connection.ssl.trustStore.password", "changeit")
      .getOrCreate()

    println(">>> Spark Session created successfully!")

    import spark.implicits._

    // -------------------
    // MySQL Connection
    // -------------------
    val mysqlUrl = MYSQL_HOST_URL
    val mysqlProps = new java.util.Properties()
    mysqlProps.setProperty("user", MYSQL_USERNAME)
    mysqlProps.setProperty("password", MYSQL_PASSWORD)

    println(">>> Connecting to MySQL and reading tables...")

    val customers = spark.read.jdbc(mysqlUrl, "customers", mysqlProps)
    println(">>> Loaded customers table. Count = " + customers.count())

    val orders = spark.read.jdbc(mysqlUrl, "orders", mysqlProps)
    println(">>> Loaded orders table. Count = " + orders.count())

    val items = spark.read.jdbc(mysqlUrl, "order_items", mysqlProps)
    println(">>> Loaded order_items table. Count = " + items.count())

    // -------------------
    // JOIN OPERATION
    // -------------------
    println(">>> Performing JOIN operation...")

    val joined = customers
      .join(orders, "customer_id")
      .join(items, "order_id")
      .select(
        $"customer_id", $"name", $"email", $"city",
        $"order_id", $"order_date", $"amount",
        $"item_id", $"product_name", $"quantity"
      )

    println(">>> Join completed. Final row count = " + joined.count())
    println(">>> Sample joined data:")
    joined.show(10, truncate = false)

    // Convert MySQL DATE → Cassandra TIMESTAMP
    val joinedFinal = joined.withColumn("order_date", $"order_date".cast("timestamp"))

    // -------------------
    // WRITE TO CASSANDRA
    // -------------------
    println(">>> Writing final dataframe to Cassandra Keyspaces...")

    try {
      joinedFinal.write
        .format("org.apache.spark.sql.cassandra")
        .option("keyspace", "retail")
        .option("table", "sales_data")
        .mode("append")
        .save()

      println(">>> Successfully written to Cassandra!")
    } catch {
      case e: Exception =>
        println(">>> ERROR WHILE WRITING TO CASSANDRA:")
        e.printStackTrace()
    }

    println("========== PIPELINE 1 COMPLETED ==========")
  }
}
