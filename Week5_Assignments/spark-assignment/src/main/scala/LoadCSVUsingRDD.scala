import org.apache.spark.sql.SparkSession

object LoadCSVUsingRDD {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Load CSV using RDD")
      .master("local[*]")
      .getOrCreate()

    def safeToDouble(s: String): Double = {
      try s.toDouble
      catch { case _: Throwable => 0.0 }
    }

    val sc = spark.sparkContext
    val rawRdd = sc.textFile("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/urbanmove_trips.csv")

    val header = rawRdd.first()
    val rows = rawRdd.filter(_ != header)

    val splitted = rows.map(line => line.split(",", -1).map(_.trim))

    // (tripId, vehicleType, distanceKm)
    val mapped = splitted.map(cols =>
      (cols(0), cols(2), safeToDouble(cols(7)))
    )

    // filter distance > 10 km
    val filtered = mapped.filter { case (_, _, dist) => dist > 10.0 }

    // output format
    val out = filtered.map { case (tripId, vehType, dist) =>
      s"$tripId,$vehType,$dist"
    }

    out.saveAsTextFile("/Users/admin/Documents/Scala Training/Scala-Training-Assignments/Week5_Assignments/spark-assignment/rdd_urbanmove_trips_out")
  }
}
