object SentencePipeline {
  val trimSpaces: String => String = _.trim
  val toLower: String => String = _.toLowerCase
  val capitalizeFirst: String => String = s => s.head.toUpper + s.tail

  val processSentence: String => String =
    trimSpaces andThen toLower andThen capitalizeFirst

  @main def testSentencePipeline(): Unit = {
    val messy = " HeLLo WOrld "
    println(processSentence(messy)) // Output: "Hello world"

    val messy2 = "   SCALA rocks  "
    println(processSentence(messy2)) // Output: "Scala rocks"
  }

}
