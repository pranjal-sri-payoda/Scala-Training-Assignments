object ShapeAnalyzer {

  // Using case classes as behavioral types
  case class Circle(r: Double)
  case class Rectangle(w: Double, h: Double)

  // Function to calculate area using pattern matching
  def area(shape: Any): Double = shape match {
    case Circle(r)       => Math.PI * r * r
    case Rectangle(w, h) => w * h
    case _               => -1.0
  }

  @main def runShapeAnalyzer(): Unit = {
    val c = Circle(3)
    val r = Rectangle(4, 5)
    val unknown = "triangle"

    println(f"Area of $c: ${area(c)}%.3f")        // 28.274
    println(s"Area of $r: ${area(r)}")        // 20.0
    println(s"Area of $unknown: ${area(unknown)}") // -1.0
  }
}
