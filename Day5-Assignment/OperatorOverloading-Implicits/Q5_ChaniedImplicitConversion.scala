object RationalDSL {

  case class Rational(n: Int, d: Int) {
    require(d != 0, "Denominator cannot be zero")

    def /(that: Rational): Rational = Rational(this.n * that.d, this.d * that.n)

    override def toString: String = s"Rational($n,$d)"
  }

  // Implicit conversion from Int to Rational
  given intToRational: Conversion[Int, Rational] with
    def apply(x: Int): Rational = Rational(x, 1)

  @main def runRationalDSL(): Unit = {
    val r = 1 / Rational(2, 3)  // Int / Rational
    println(r)                   // Rational(3,2)

    val r2 = Rational(4, 5) / Rational(2, 3)
    println(r2)                  // Rational(12,10)

    val normal = 6 / 2
    println(normal)              // 3
  }
}
