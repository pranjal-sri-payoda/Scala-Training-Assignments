package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise3 {
  def main(args: Array[String]): Unit = {
    println("===== EX3 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise3").master("local[*]").getOrCreate()
    import spark.implicits._

    val levels = Array("INFO", "WARN", "ERROR")
    val numLogs = 5000000

    val logsRDD = spark.sparkContext.parallelize(1 to numLogs, 40).map { _ =>
      val ts = System.currentTimeMillis() - Random.nextInt(10000000)
      val level = levels(Random.nextInt(levels.length))
      val msg = Random.alphanumeric.take(15).mkString
      val user = Random.nextInt(10000)
      s"$ts|$level|$msg|$user"
    }

    println("Generated logs RDD")

    // RDD filter for ERROR
    val errRDD = logsRDD.filter(line => line.split("\\|")(1) == "ERROR")
    val errCountRDD = errRDD.count()
    println(s"ERROR count (RDD): $errCountRDD")

    // DataFrame conversion & filter
    val logsDF = logsRDD.map(_.split("\\|")).map(a => (a(0), a(1), a(2), a(3))).toDF("timestamp", "level", "message", "userId")
    val errDF = logsDF.filter($"level" === "ERROR")
    val errCountDF = errDF.count()
    println(s"ERROR count (DF): $errCountDF")

    // Write ERROR logs to plain text (RDD) and full logs to JSON (DF)
    val out = "output/ex3"
    errRDD.saveAsTextFile(s"$out/errors_text")               // plain text
    logsDF.write.mode("overwrite").json(s"$out/logs_json")  // json

    println("Wrote error text and logs JSON")

    /*
      Observations:
      - Writing plain text (many small files) can be slower because no columnar / block compression,
        and text doesn't benefit from columnar formats or predicate pushdown.
      - filter is a narrow transformation; map is narrow; sort/orderBy is broad (shuffle).
    */
    Thread.sleep(100000)
    spark.stop()
    println("===== EX3 DONE =====")
  }
}
