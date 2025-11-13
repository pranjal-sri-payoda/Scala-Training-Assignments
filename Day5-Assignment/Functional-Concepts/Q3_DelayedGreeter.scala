object DelayedGreeter {

  def delayedMessage(delayMs: Int)(message: String): Unit = {
    Thread.sleep(delayMs) // wait for specified milliseconds
    println(message) // print the message
  }

  // delayedMessage(1500)_ : "_" is no longer supported in Scala 3
  val oneSecondSay = delayedMessage(1500) // binds delay to 1500ms

  @main def runDelayedGreeter(): Unit = {
    oneSecondSay("Hello!")
    oneSecondSay("How are you?")
    oneSecondSay("Goodbye!")
  }

}
