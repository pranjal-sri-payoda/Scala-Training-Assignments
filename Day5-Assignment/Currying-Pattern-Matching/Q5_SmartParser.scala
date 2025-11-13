object SmartParser {

  def safeDivide(x: Int, y: Int): Option[Int] =
    if (y == 0) None else Some(x / y)

  def parseAndDivide(input: String): Either[String, Int] = {
    input.toIntOption match {
      case None => Left("Invalid number") // parsing failed
      case Some(num) =>
        safeDivide(100, num) match {
          case None        => Left("Division by zero")
          case Some(result) => Right(result)           // successful division
        }
    }
  }

  // Main method to test
  @main def runSmartParser(): Unit = {
    println(parseAndDivide("25")) 
    println(parseAndDivide("0"))
    println(parseAndDivide("abc"))
  }
}
