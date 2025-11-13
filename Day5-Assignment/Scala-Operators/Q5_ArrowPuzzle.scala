@main def animalSounds(): Unit = {
  val animals = Map(
    "dog" -> "bark",
    "cat" -> "meow",
    "cow" -> "moo"
  )

  val animals2 = animals + ("lion" -> "roar")

  println(s"Sound of cow: ${animals2("cow")}")                // moo
  println(animals2.getOrElse("tiger", "unknown"))             // unknown
}