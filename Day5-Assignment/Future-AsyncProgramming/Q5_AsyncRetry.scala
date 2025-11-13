import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

object AsyncRetry{

  // Simulated server fetch — 50% chance to fail
  def fetchDataFromServer(server: String): Future[String] = Future {
    if (scala.util.Random.nextBoolean()) 
      throw new RuntimeException(s"$server failed!")
    else 
      s"$server: Data fetched successfully"
  }

  // Recursive retry mechanism
  def fetchWithRetry(server: String, maxRetries: Int): Future[String] =
    fetchDataFromServer(server).recoverWith {
      case ex if maxRetries > 0 =>
        println(s"Retrying $server... remaining attempts: ${maxRetries}")
        fetchWithRetry(server, maxRetries - 1)
    }

  @main def runAsyncRetry(): Unit =
    val server = "Server-1"
    val result = fetchWithRetry(server, 3)

    result.onComplete {
      case Success(data) => println(s"Success: $data")
      case Failure(ex)   => println(s"All retries failed: $ex")
    }

    // Keep JVM alive for demonstration
    Thread.sleep(2000)
}