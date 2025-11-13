class Counter(val value: Int) {
  // Overload + to accept another Counter
  def +(that: Counter): Int = this.value + that.value

  // Overload + to accept an Int
  def +(that: Int): Int = this.value + that
}

@main def testCounter(): Unit = {
  val a = new Counter(5)
  val b = new Counter(7)

  println(a + b)   // prints 12
  println(a + 10)  // prints 15
}
