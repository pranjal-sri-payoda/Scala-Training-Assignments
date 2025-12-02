package spark_exercise

import org.apache.spark.sql.SparkSession
import scala.util.Random
import org.apache.spark.sql.functions._

object Exercise8 {
  def main(args: Array[String]): Unit = {
    println("===== EX8 MAIN =====")
    val spark = SparkSession.builder().appName("Exercise8").master("local[*]").getOrCreate()
    import spark.implicits._

    val numEmp = 1000000

    val empRDD = spark.sparkContext.parallelize(1 to numEmp, 20).map { id =>
      val name = Random.alphanumeric.take(7).mkString
      val deptArr: Array[String] = Array("HR","IT","Sales","Finance")   // FIXED
      val dept = deptArr(Random.nextInt(4))
      val salary = 30000 + Random.nextInt(70000)
      (id, name, dept, salary)
    }

    val empDF = empRDD.toDF("empId", "name", "department", "salary")
    println("Generated emp DF")

    val avgSal = empDF.groupBy("department").agg(avg("salary").as("avg_salary"))
    avgSal.show()

    val out = "output/ex8"
    avgSal.write.mode("overwrite").csv(s"$out/avg_salary_csv")

    // Load CSV back and compare schema inference
    val loadedDefault = spark.read.option("header","false").csv(s"$out/avg_salary_csv") // default: all string-like
    println("Schema when read without inferSchema:")
    loadedDefault.printSchema()

    val loadedInfer = spark.read.option("inferSchema","true").csv(s"$out/avg_salary_csv")
    println("Schema when read with inferSchema=true:")
    loadedInfer.printSchema()

    /*
      Observations:
      - By default CSV readers treat columns as strings unless you explicitly enable inferSchema or provide a schema.
      - Parquet preserves types automatically.
    */
    Thread.sleep(10000)
    spark.stop()
    println("===== EX8 DONE =====")
  }
}
