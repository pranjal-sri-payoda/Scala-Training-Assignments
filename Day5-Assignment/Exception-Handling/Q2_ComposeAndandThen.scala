object ComposeAndandThen {

  val trim: String => String = _.trim
  val toInt: String => Int = _.toInt
  val doubleIt: Int => Int = _ * 2

  // renamed to avoid duplicate 'runPipeline' symbol
  @main def runFunctionPipeline(): Unit = {
    val input = " 21 "

    // Compose: f compose g => f(g(x))
    val pipelineCompose = doubleIt.compose(toInt).compose(trim)
    println(s"Using compose: '$input' -> ${pipelineCompose(input)}") // 42

    // AndThen: f andThen g => g(f(x))
    val pipelineAndThen = trim.andThen(toInt).andThen(doubleIt)
    println(s"Using andThen: '$input' -> ${pipelineAndThen(input)}") // 42

    // Attempting wrong compose order: will not compile
    // val wrongCompose = trim.compose(toInt) 
  }
}