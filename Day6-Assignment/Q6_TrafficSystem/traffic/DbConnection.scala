package traffic

import java.sql.{Connection, DriverManager, SQLException}

object DatabaseConnection {

  // JDBC connection parameters
  val jdbcUrl = "jdbc:mysql://localhost:3306/pranjal?useSSL=true"
  val username = "mysqladmin"
  val password = "Password"

  // Load JDBC Driver
  try {
    Class.forName("com.mysql.cj.jdbc.Driver")
    println("MySQL JDBC Driver Registered")
  } catch {
    case e: ClassNotFoundException => println("MySQL JDBC Driver not found"); e.printStackTrace()
  }

  // Get Connection
  def getConnection: Connection = {
    try {
      DriverManager.getConnection(jdbcUrl, username, password)
    } catch {
      case e: SQLException =>
        println("Connection Failed! Check output console")
        e.printStackTrace()
        throw e
    }
  }

  // Create Tables if not exists
  def createTables(): Unit = {
    val conn = getConnection
    try {
      val stmt = conn.createStatement()

      // Vehicles table
      stmt.execute(
        """CREATE TABLE IF NOT EXISTS Vehicles (
          |vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
          |license_plate VARCHAR(20),
          |vehicle_type VARCHAR(20),
          |owner_name VARCHAR(100)
          |)""".stripMargin)

      // TrafficSignals table
      stmt.execute(
        """CREATE TABLE IF NOT EXISTS TrafficSignals (
          |signal_id INT PRIMARY KEY AUTO_INCREMENT,
          |location VARCHAR(100),
          |status VARCHAR(10)
          |)""".stripMargin)

      // Violations table
      stmt.execute(
        """CREATE TABLE IF NOT EXISTS Violations (
          |violation_id INT PRIMARY KEY AUTO_INCREMENT,
          |vehicle_id INT,
          |signal_id INT,
          |violation_type VARCHAR(50),
          |timestamp DATETIME,
          |FOREIGN KEY (vehicle_id) REFERENCES Vehicles(vehicle_id),
          |FOREIGN KEY (signal_id) REFERENCES TrafficSignals(signal_id)
          |)""".stripMargin)

      println("Tables are ready.")
      stmt.close()
    } finally {
      conn.close()
    }
  }
}
