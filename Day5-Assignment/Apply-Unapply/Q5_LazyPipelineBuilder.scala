object LazyPipelineBuilder {

  // Companion object that builds a lazy pipeline
  object Pipeline {
    def apply[T](block: => T): LazyPipeline[T] = new LazyPipeline(block)
  }

  // Class that represents a lazily evaluated pipeline stage
  class LazyPipeline[T](block: => T) {
    lazy val result: T = block  // deferred evaluation

    def map[R](f: T => R): LazyPipeline[R] = Pipeline {
      f(result)  // apply transformation lazily
    }
  }

  @main def runLazyPipeline(): Unit = {
    val p = Pipeline {
      println("Step 1: Preparing data")
      List(1, 2, 3)
    }.map { xs =>
      println("Step 2: Transforming data")
      xs.map(_ * 2)
    }

    println("Before accessing pipeline...")
    println("Result: " + p.result)
  }
}
