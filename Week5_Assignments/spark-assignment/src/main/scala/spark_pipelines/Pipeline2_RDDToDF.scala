package spark_pipelines

import org.apache.spark.sql.SparkSession

object Pipeline2_RDDToDF {

  def safeToLong(s: String): Long = try s.toLong catch { case _: Exception => 0L }
  def safeToInt(s: String): Int = try s.toInt catch { case _: Exception => 0 }
  def safeToDouble(s: String): Double = try s.toDouble catch { case _: Exception => 0.0 }

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("RDD to DF Example")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext
    val rawRdd = sc.textFile("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/urbanmove_trips.csv")

    val header = rawRdd.first()
    val rows = rawRdd.filter(_ != header)

    val splitted = rows.map(_.split(",", -1).map(_.trim))

    // Convert RDD → RDD[Trip]
    val tripRdd = splitted.map { cols =>
      Trip(
        safeToLong(cols(0)),      // tripId
        safeToInt(cols(1)),       // driverId
        cols(2),                  // vehicleType
        cols(3),                  // startTime
        cols(4),                  // endTime
        cols(5),                  // startLocation
        cols(6),                  // endLocation
        safeToDouble(cols(7)),    // distanceKm
        safeToDouble(cols(8)),    // fareAmount
        cols(9),                  // paymentMethod
        safeToDouble(cols(10))    // customerRating
      )
    }

    // Convert to DataFrame
    val tripDF = spark.createDataFrame(tripRdd)

    tripDF.show(5, truncate = false)
    tripDF.printSchema()
  }
}

case class Trip(
                 tripId: Long,
                 driverId: Int,
                 vehicleType: String,
                 startTime: String,
                 endTime: String,
                 startLocation: String,
                 endLocation: String,
                 distanceKm: Double,
                 fareAmount: Double,
                 paymentMethod: String,
                 customerRating: Double
               )