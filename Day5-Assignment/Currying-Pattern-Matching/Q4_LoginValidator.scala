object LoginValidator {

  // Function to validate login credentials
  def validateLogin(username: String, password: String): Either[String, String] = {
    if (username.isEmpty) Left("Username missing")
    else if (password.isEmpty) Left("Password missing")
    else Right("Login successful")
  }

  @main def runLoginValidator(): Unit = {
    val test1 = validateLogin("", "123")
    val test2 = validateLogin("user", "")
    val test3 = validateLogin("user", "123")

    println(test1) 
    println(test2) 
    println(test3) 
  }
}
