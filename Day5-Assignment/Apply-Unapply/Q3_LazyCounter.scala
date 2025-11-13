object LazyEvaluationDemo {

  // Class with a lazy val that tracks how many times it was computed
  class LazyCounter {
    private var computeCount = 0

    lazy val value: Int = {
      computeCount += 1
      println("Computing value...")
      42
    }

    def getCount: Int = computeCount
  }

  // Entry point
  @main def runLazyEvaluation(): Unit = {
    val c = new LazyCounter

    println("Before first access")
    println(c.value) // first access → triggers computation
    println("Access again")
    println(c.value) // second access → uses cached value
    println("Compute count: " + c.getCount)
  }
}
