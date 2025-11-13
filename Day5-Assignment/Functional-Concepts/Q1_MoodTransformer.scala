object MoodTransformer {

  // Higher-order function: returns a function that remembers 'prefix'
  def moodChanger(prefix: String): String => String = {
    word => s"$prefix-$word-$prefix"
  }

  @main def runMoodTransformer(): Unit = {
    // Create different mood functions
    val happyMood = moodChanger("happy")
    val angryMood = moodChanger("angry")
    val sadMood   = moodChanger("sad")

    // Test the moods
    println(happyMood("day"))    // Output: happy-day-happy
    println(angryMood("crowd"))  // Output: angry-crowd-angry
    println(sadMood("news"))     // Output: sad-news-sad

    // Additional test
    val excitedMood = moodChanger("excited")
    println(excitedMood("party").toUpperCase) // Output: EXCITED-PARTY-EXCITED
  }
}
