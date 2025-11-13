import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._

object FutureSequencing{

  def task(name: String, delay: Int): Future[String] = Future {
    Thread.sleep(delay)
    s"$name done"
  }

  @main def runFutureSequencing(): Unit =
    // Sequential execution
    val startSeq = System.currentTimeMillis()
    val sequential: Future[List[String]] = 
      task("Task1", 1000).flatMap { r1 =>
        task("Task2", 2000).flatMap { r2 =>
          task("Task3", 1500).map { r3 =>
            List(r1, r2, r3)
          }
        }
      }

    sequential.onComplete {
      case Success(results) =>
        val endSeq = System.currentTimeMillis()
        println(s"Sequential results: $results")
        println(s"Sequential time: ${endSeq - startSeq} ms\n")
      case Failure(ex) =>
        println(s"Sequential failed: $ex")
    }

    // Parallel execution
    val startPar = System.currentTimeMillis()
    val parallel: Future[List[String]] = Future.sequence(
      List(
        task("Task1", 1000),
        task("Task2", 2000),
        task("Task3", 1500)
      )
    )

    parallel.onComplete {
      case Success(results) =>
        val endPar = System.currentTimeMillis()
        println(s"Parallel results: $results")
        println(s"Parallel time: ${endPar - startPar} ms")
      case Failure(ex) =>
        println(s"Parallel failed: $ex")
    }

    // Wait for both to complete (only needed in main scripts to prevent exit)
    Await.result(sequential, 10.seconds)
    Await.result(parallel, 10.seconds)
}