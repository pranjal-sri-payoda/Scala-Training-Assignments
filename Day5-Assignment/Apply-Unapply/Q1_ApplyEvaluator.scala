object EvaluatorExample {

  // Evaluator object that accepts a deferred code block
  object Evaluator:
    def apply(block: => Any): Unit =
      println("Evaluating block...")
      val result = block
      println(s"Result = $result")

  @main def runEvaluator(): Unit =
    Evaluator {
      val x = 5
      val y = 3
      x * y + 2
    }

    Evaluator {
      val msg = "Hello"
      msg.reverse
    }

    Evaluator {
      (1 to 5).sum
    }
}