object PersonOrderingDemo {

  case class Person(name: String, age: Int)

  // Implicit class to add comparison operators based on age
  implicit class PersonOps(p1: Person) {
    def <(p2: Person): Boolean = p1.age < p2.age
    def >(p2: Person): Boolean = p1.age > p2.age
    def <=(p2: Person): Boolean = p1.age <= p2.age
    def >=(p2: Person): Boolean = p1.age >= p2.age
  }

  @main def runPersonOrdering(): Unit = {
    val p1 = Person("Ravi", 25)
    val p2 = Person("Meena", 30)

    println(p1 < p2)   // true
    println(p1 >= p2)  // false
    println(p2 > p1)   // true
    println(p2 <= p1)  // false

    if (p1 < p2) println(s"${p1.name} is younger than ${p2.name}")
  }
}
