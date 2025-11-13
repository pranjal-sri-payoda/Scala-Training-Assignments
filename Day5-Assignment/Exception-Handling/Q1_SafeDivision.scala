object SafeDivision {

  def safeDivide(a: Int, b: Int): Either[String, Double] =
    if b == 0 then Left("Division by zero")
    else Right(a.toDouble / b)

  @main def runSafeDivide(): Unit = {
    val pairs = List((10, 2), (5, 0), (8, 4))

    // Map over pairs using safeDivide
    val results: List[Either[String, Double]] = pairs.map { case (a, b) => safeDivide(a, b) }

    // Collect only Right values
    val validResults: List[Double] = results.collect { case Right(value) => value }

    // Collect only Left values
    val errors: List[String] = results.collect { case Left(err) => err }

    println(s"Valid results: $validResults")
    println(s"Errors: $errors")
  }
}
