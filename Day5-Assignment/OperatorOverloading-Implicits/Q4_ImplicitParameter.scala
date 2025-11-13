object MoneyDSL {

  case class Money(amount: Double) {

    def +(that: Money)(using precision: Double): Money =
      Money(roundToPrecision(this.amount + that.amount))

    def -(that: Money)(using precision: Double): Money =
      Money(roundToPrecision(this.amount - that.amount))

    private def roundToPrecision(value: Double)(using precision: Double): Double =
      (math.round(value / precision) * precision).formatted("%.2f").toDouble

    override def toString: String = f"₹$amount%.2f"
  }

  @main def runMoneyDSL(): Unit = {
    given roundingPrecision: Double = 0.05 

    val m1 = Money(10.23)
    val m2 = Money(5.19)

    println(m1 + m2) // ₹15.20
    println(m1 - m2) // ₹5.05

    {
      given Double = 0.10
      println(m1 + m2) // ₹15.30 with 0.10 precision
    }

    println(m1 + m2) // ₹15.20
  }
}
