import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._

object FutureRecoveryDemo{

  def riskyOperation(): Future[Int] = Future {
    val n = scala.util.Random.nextInt(3)
    if n == 0 then throw new RuntimeException("Failed!")
    else n
  }

  @main def runFutureRecovery(): Unit =

    println("=== Using recover (fallback value) ===")
    val recovered: Future[Int] = riskyOperation().recover {
      case _: RuntimeException => -1
    }

    recovered.onComplete {
      case Success(value) => println(s"Result with recover: $value")
      case Failure(ex)    => println(s"Unexpected failure: $ex")
    }

    // ---- recoverWith to retry once ----
    println("\n=== Using recoverWith (retry once) ===")
    val retryOnce: Future[Int] = riskyOperation().recoverWith {
      case _: RuntimeException =>
        println("First attempt failed, retrying...")
        riskyOperation()
    }

    retryOnce.onComplete {
      case Success(value) => println(s"Result with recoverWith: $value")
      case Failure(ex)    => println(s"Both attempts failed: $ex")
    }

    // Wait for futures to finish (necessary in a script)
    Await.result(recovered, 5.seconds)
    Await.result(retryOnce, 5.seconds)
}