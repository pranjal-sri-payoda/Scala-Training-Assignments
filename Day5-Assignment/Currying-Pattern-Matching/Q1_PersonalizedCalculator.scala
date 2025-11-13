object PersonalizedCalculator {

  def calculate(op: String)(x: Int, y: Int): Int = op.toLowerCase match {
    case "add" => x + y
    case "sub" => x - y
    case "mul" => x * y
    case "div" =>
      if (y != 0) x / y
      else
        throw new IllegalArgumentException("Division by zero is not allowed.")
    case _ => throw new IllegalArgumentException("Invalid operation.")
  }

  @main def testCalculator(): Unit = {

    val add = calculate("add") // partially applied function
    val subtract = calculate("sub")
    val multiply = calculate("mul")
    val divide = calculate("div")

    println(s"Addition of 10 and 5 = ${add(10, 5)}")       // 15
    println(s"Subtraction of 10 and 5 = ${subtract(10, 5)}")// 5
    println(s"Multiplication of 3 and 4 = ${multiply(3, 4)}")// 12
    println(s"Division of 20 by 5 = ${divide(20, 5)}")     // 4

  }

}
