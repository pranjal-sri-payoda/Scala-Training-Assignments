package traffic

import java.sql.{Connection, PreparedStatement, ResultSet, Timestamp}
import scala.io.StdIn
import java.time.LocalDateTime
import DatabaseConnection._

object TrafficMain {

  def main(args: Array[String]): Unit = {

    // Create tables first
    createTables()

    var continue = true
    while (continue) {
      println(
        """|
           |1. Add Vehicle
           |2. Add Traffic Signal
           |3. Record Violation
           |4. Update Signal Status
           |5. View Vehicles
           |6. View Traffic Signals
           |7. View Violations
           |8. Exit
           |Enter choice:""".stripMargin)

      val choice = StdIn.readInt()
      choice match {
        case 1 => addVehicle()
        case 2 => addTrafficSignal()
        case 3 => recordViolation()
        case 4 => updateSignalStatus()
        case 5 => viewVehicles()
        case 6 => viewSignals()
        case 7 => viewViolations()
        case 8 => 
          println("Exiting...")
          continue = false
        case _ => println("Invalid choice!")
      }
    }
  }

  def addVehicle(): Unit = {
    val conn = getConnection
    try {
      print("License Plate: "); val plate = StdIn.readLine()
      print("Vehicle Type (car/bike/truck): "); val vType = StdIn.readLine()
      print("Owner Name: "); val owner = StdIn.readLine()

      val stmt: PreparedStatement = conn.prepareStatement(
        "INSERT INTO Vehicles(license_plate, vehicle_type, owner_name) VALUES (?, ?, ?)"
      )
      stmt.setString(1, plate)
      stmt.setString(2, vType)
      stmt.setString(3, owner)
      stmt.executeUpdate()
      println("Vehicle added successfully.")
      stmt.close()
    } finally conn.close()
  }

  def addTrafficSignal(): Unit = {
    val conn = getConnection
    try {
      print("Location: "); val location = StdIn.readLine()
      print("Status (green/yellow/red): "); val status = StdIn.readLine()

      val stmt: PreparedStatement = conn.prepareStatement(
        "INSERT INTO TrafficSignals(location, status) VALUES (?, ?)"
      )
      stmt.setString(1, location)
      stmt.setString(2, status)
      stmt.executeUpdate()
      println("Traffic signal added successfully.")
      stmt.close()
    } finally conn.close()
  }

  def recordViolation(): Unit = {
    val conn = getConnection
    try {
      print("Vehicle ID: "); val vehicleId = StdIn.readInt()
      print("Signal ID: "); val signalId = StdIn.readInt()
      print("Violation Type (speeding/signal jump): "); val vType = StdIn.readLine()

      val stmt: PreparedStatement = conn.prepareStatement(
        "INSERT INTO Violations(vehicle_id, signal_id, violation_type, timestamp) VALUES (?, ?, ?, ?)"
      )
      stmt.setInt(1, vehicleId)
      stmt.setInt(2, signalId)
      stmt.setString(3, vType)
      stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()))
      stmt.executeUpdate()
      println("Violation recorded successfully.")
      stmt.close()
    } finally conn.close()
  }

  def updateSignalStatus(): Unit = {
    val conn = getConnection
    try {
      print("Signal ID: "); val signalId = StdIn.readInt()
      print("New Status (green/yellow/red): "); val status = StdIn.readLine()

      val stmt: PreparedStatement = conn.prepareStatement(
        "UPDATE TrafficSignals SET status=? WHERE signal_id=?"
      )
      stmt.setString(1, status)
      stmt.setInt(2, signalId)
      val rows = stmt.executeUpdate()
      if (rows > 0) println("Signal status updated.")
      else println("Signal ID not found.")
      stmt.close()
    } finally conn.close()
  }

  def viewVehicles(): Unit = {
    val conn = getConnection
    try {
      val stmt = conn.createStatement()
      val rs: ResultSet = stmt.executeQuery("SELECT * FROM Vehicles")
      println("Vehicles:")
      while (rs.next()) {
        println(s"ID: ${rs.getInt("vehicle_id")}, Plate: ${rs.getString("license_plate")}, Type: ${rs.getString("vehicle_type")}, Owner: ${rs.getString("owner_name")}")
      }
      rs.close()
      stmt.close()
    } finally conn.close()
  }

  def viewSignals(): Unit = {
    val conn = getConnection
    try {
      val stmt = conn.createStatement()
      val rs: ResultSet = stmt.executeQuery("SELECT * FROM TrafficSignals")
      println("Traffic Signals:")
      while (rs.next()) {
        println(s"ID: ${rs.getInt("signal_id")}, Location: ${rs.getString("location")}, Status: ${rs.getString("status")}")
      }
      rs.close()
      stmt.close()
    } finally conn.close()
  }

  def viewViolations(): Unit = {
    val conn = getConnection
    try {
      val stmt = conn.createStatement()
      val rs: ResultSet = stmt.executeQuery(
        """SELECT v.violation_id, v.vehicle_id, v.signal_id, v.violation_type, v.timestamp
          |FROM Violations v""".stripMargin)
      println("Violations:")
      while (rs.next()) {
        println(s"ID: ${rs.getInt("violation_id")}, Vehicle ID: ${rs.getInt("vehicle_id")}, Signal ID: ${rs.getInt("signal_id")}, Type: ${rs.getString("violation_type")}, Timestamp: ${rs.getTimestamp("timestamp")}")
      }
      rs.close()
      stmt.close()
    } finally conn.close()
  }
}
