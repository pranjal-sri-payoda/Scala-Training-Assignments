object SelectiveTransformation {

  @main def runSelectiveTransform(): Unit = {
    val items: List[Any] = List(1, "apple", 3.5, "banana", 42)

    // Partial function: only defined for Int
    val doubleInts: PartialFunction[Any, Int] = {
      case i: Int => i * 2
    }

    // Apply collect to filter + map in one step
    val result: List[Int] = items.collect(doubleInts)

    println(result)  // Output: List(2, 84)
  }
}
