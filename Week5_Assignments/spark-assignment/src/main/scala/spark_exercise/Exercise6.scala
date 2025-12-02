package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random

object Exercise6 {
  def main(args: Array[String]): Unit = {
    println("===== EX6 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise6").master("local[*]").getOrCreate()
    import spark.implicits._

    val userCount = 1000000
    val postCount = 2000000

    val userRDD = spark.sparkContext.parallelize(1 to userCount, 30).map { id =>
      val name = Random.alphanumeric.take(8).mkString
      val age = 15 + Random.nextInt(60)
      (id, name, age)
    }

    val postRDD = spark.sparkContext.parallelize(1 to postCount, 40).map { pid =>
      val user = Random.nextInt(userCount) + 1
      val txt = Random.alphanumeric.take(20).mkString
      (pid, user, txt)
    }

    val userDF = userRDD.toDF("userId", "name", "age")
    val postDF = postRDD.toDF("postId", "userId", "text")

    println("Joining users and posts (DF join)")
    val joined = userDF.join(postDF, Seq("userId"))
    println("Joined sample:")
    joined.show(5, truncate = false)

    // Count posts per age group (age bucket example)
    val postsPerAge = joined.groupBy("age").count().orderBy("age")
    postsPerAge.show(10, truncate = false)

    // write results to JSON
    val out = "output/ex6"
    postsPerAge.write.mode("overwrite").json(s"$out/posts_per_age_json")

    println("Wrote posts per age JSON")

    /*
      Observations:
      - Join causes shuffle when keys are evenly distributed across partitions and Spark must bring matching keys together.
      - DataFrame join is easier because Catalyst handles optimization (broadcast join, shuffle hash join) and you can express join declaratively.
      - RDD joins require manual pairing and are more verbose and error-prone.
    */
    Thread.sleep(10000)
    spark.stop()
    println("===== EX6 DONE =====")
  }
}
