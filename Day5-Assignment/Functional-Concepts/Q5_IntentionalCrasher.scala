object IntentionalCrasher {
  def main(args: Array[String]): Unit = {

    val safeDivide: PartialFunction[Int, String] = {
      case x if x != 0 => s"Result: ${100 / x}"
    }

    // Lift to make it total (returning Option)
    val safe: Int => Option[String] = safeDivide.lift

    println(safe(10))
    println(safe(0))
    println(safe(25))

    safe(0) match {
      case Some(result) => println(result)
      case None => println("Division by zero is undefined!")
    }
  }
}
