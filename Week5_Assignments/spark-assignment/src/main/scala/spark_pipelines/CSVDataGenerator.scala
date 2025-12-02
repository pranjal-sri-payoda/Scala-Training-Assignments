package spark_pipelines

import java.io._
import java.time._
import java.time.format.DateTimeFormatter
import scala.util.Random

object CSVDataGenerator extends App {
  val file = new PrintWriter(new File("urbanmove_trips.csv"))
  val areas = Array("MG Road","Indira Nagar","Koramangala","Whitefield","Marathahalli","HSR Layout","BTM","Jayanagar")
  val vehicleTypes = Array("AUTO","TAXI","BIKE")
  val paymentMethods = Array("CASH","UPI","CARD")
  val rand = new Random(42L) // deterministic seed for repeatability
  val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  def randomDateTime(): LocalDateTime = {
    val now = LocalDateTime.now()
    // up to ~70 days back (100k minutes ~ 69.4 days)
    now.minusMinutes(rand.nextInt(100000))
  }

  file.write("tripId,driverId,vehicleType,startTime,endTime,startLocation,endLocation,distanceKm,fareAmount,paymentMethod,customerRating\n")

  val total = 1000000
  for(i <- 1 to total) {
    val start = randomDateTime()
    val durationMinutes = rand.nextInt(50) + 1 // 1 - 50 minutes
    val end = start.plusMinutes(durationMinutes)
    val distance = BigDecimal(1 + rand.nextDouble() * 15).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    // fare = distance * (baseRate + perKm random) with a minimum
    val base = 20 + rand.nextInt(11) // 20..30 rupee-ish base multiplier
    val fare = BigDecimal(distance * base).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    val rating = BigDecimal(1.0 + rand.nextDouble() * 4.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

    // CSV-safe (no embedded commas expected in fields, but safe pattern)
    file.write(s"$i,${rand.nextInt(5000)},${vehicleTypes(rand.nextInt(vehicleTypes.length))},${start.format(formatter)},${end.format(formatter)},${areas(rand.nextInt(areas.length))},${areas(rand.nextInt(areas.length))},$distance,$fare,${paymentMethods(rand.nextInt(paymentMethods.length))},$rating\n")
    if(i % 100000 == 0) println(s"Written $i / $total")
  }

  file.close()
  println(s"Wrote $total rows to urbanmove_trips.csv")
}
