object UnapplyChain {

  case class Address(city: String, pincode: Int)
  case class Person(name: String, address: Address)

  @main def runUnapplyChain(): Unit = {
    val p = Person("Ravi", Address("Chennai", 600001))
    val q = Person("Arun", Address("Mumbai", 400001))

    // Nested pattern matching + pattern guard
    p match {
      case Person(_, Address(city, pin)) if city.startsWith("C") =>
        println(s"$city - $pin")
      case _ =>
        println("No match")
    }

    // Another example to show the guard failing
    q match {
      case Person(_, Address(city, pin)) if city.startsWith("C") =>
        println(s"$city - $pin")
      case _ =>
        println("No match")
    }
  }
}
