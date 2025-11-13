object DualNatureExample {

  object Email {
    def apply(user: String, domain: String): String =
      s"$user@$domain"

    def unapply(email: String): Option[(String, String)] = {
      val parts = email.split("@")
      if (parts.length == 2) Some((parts(0), parts(1))) else None
    }
  }

  @main def runDualNatureExample(): Unit = {
    // Construct email using apply
    val e = Email("alice", "mail.com")
    println(e) // Output: alice@mail.com

    // Deconstruct using unapply
    e match {
      case Email(user, domain) =>
        println(s"User: $user, Domain: $domain")
      case _ =>
        println("Invalid email")
    }

    // Try an invalid email
    val invalid = "bobmail.com"
    invalid match {
      case Email(user, domain) =>
        println(s"User: $user, Domain: $domain")
      case _ =>
        println(s"'$invalid' is not a valid email")
    }
  }
}
