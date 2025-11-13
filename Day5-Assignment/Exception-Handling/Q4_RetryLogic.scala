import scala.util.Try

object RetryDemo {

  def fetchData(): Int = {
    val n = scala.util.Random.nextInt(3)
    if (n == 0) throw new RuntimeException("Network fail")
    n
  }

  def retry[T](times: Int)(op: => T): Option[T] = {
    if (times <= 0) None
    else Try(op).toOption match {
      case some @ Some(_) => some
      case None           => retry(times - 1)(op)
    }
  }

  @main def runRetryDemo(): Unit = {
    val data = retry(3)(fetchData())
    println(data)  // Some(result) or None if all retries failed
  }
}
