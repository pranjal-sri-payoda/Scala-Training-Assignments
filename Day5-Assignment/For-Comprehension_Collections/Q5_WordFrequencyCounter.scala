object WordFrequencyCounter {
  def main(args: Array[String]): Unit = {
    def wordFrequency(lines: List[String]): Map[String, Int] = {
      // Use for-comprehension to flatten all words across all lines
      val words =
        for {
          line <- lines // iterate over each sentence
          word <- line.split(" ") // split into individual words
        } yield word

      // Group identical words and count occurrences
      val freq: Map[String, Int] = words
        .groupBy(identity)
        .view
        .mapValues(_.size)
        .toMap

      freq
    }

    val lines = List(
      "Scala is powerful",
      "Scala is concise",
      "Functional programming is powerful"
    )

    val frequency = wordFrequency(lines)
    println(frequency)
  }
}
