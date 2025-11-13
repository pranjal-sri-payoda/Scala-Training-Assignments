object VectorAlgebraDSL {

  case class Vec2D(x: Double, y: Double) {

    def +(that: Vec2D): Vec2D = Vec2D(this.x + that.x, this.y + that.y)

    def -(that: Vec2D): Vec2D = Vec2D(this.x - that.x, this.y - that.y)

    // Scalar multiplication (Vec * scalar)
    def *(scalar: Double): Vec2D = Vec2D(this.x * scalar, this.y * scalar)

    // Pretty output
    override def toString: String = s"Vec2D($x, $y)"
  }

  // Implicit conversion for Double * Vec2D
  given Conversion[Double, ScalarOps] with
    def apply(scalar: Double): ScalarOps = new ScalarOps(scalar)

  // Implicit conversion for Int * Vec2D
  given Conversion[Int, ScalarOps] with
    def apply(scalar: Int): ScalarOps = new ScalarOps(scalar.toDouble)

  // Helper class that defines scalar * vector
  class ScalarOps(val scalar: Double) {
    def *(v: Vec2D): Vec2D = v * scalar
  }

  // Entry point
  @main def runVectorDSL(): Unit = {
    val v1 = Vec2D(2, 3)
    val v2 = Vec2D(4, 1)

    println(v1 + v2)  // Vec2D(6.0, 4.0)
    println(v1 - v2)  // Vec2D(-2.0, 2.0)
    println(v1 * 3)   // Vec2D(6.0, 9.0)
    println(3 * v1)   // Vec2D(6.0, 9.0)
  }
}
