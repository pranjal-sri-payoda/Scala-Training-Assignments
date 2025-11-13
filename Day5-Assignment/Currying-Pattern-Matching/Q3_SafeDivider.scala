object SafeDivider {

  def safeDivide(x: Int, y: Int): Option[Int] = {
    if (y == 0) None
    else Some(x / y)
  }

  @main 
  def runSafeDivider(): Unit = {
    val result1 = safeDivide(10, 0).getOrElse(-1)
    val result2 = safeDivide(20, 4).getOrElse(-1)

    println(s"10 / 0 = $result1") 
    println(s"20 / 4 = $result2")
  }
}
