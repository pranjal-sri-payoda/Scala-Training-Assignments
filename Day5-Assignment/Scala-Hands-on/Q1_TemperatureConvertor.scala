object TemperatureConvertor {
  def main(args: Array[String]): Unit = {
    def convertTemp(value: Double, scale: String): Double = {
      val convertedTempValue: Double = scale.toLowerCase() match {
        case "c" => (value * 9 / 5) + 32
        case "f" => (value - 32) * 5 / 9
        case _   => value
      }

      convertedTempValue
    }

    println(f"0°C -> ${convertTemp(0, "C")}%.1f °F")                 // 32.0
    println(f"212°F -> ${convertTemp(212, "F")}%.1f °C")             // 100.0
    println(f"50 (unknown scale 'X') -> ${convertTemp(50, "X")}%.1f (no conversion)") // 50.0
  }

}