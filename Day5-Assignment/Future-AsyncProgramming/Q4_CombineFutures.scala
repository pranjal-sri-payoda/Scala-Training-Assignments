import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

object FutureCombineDemo{

  @main def runFutureCombine(): Unit =

    val f1 = Future { Thread.sleep(1000); 10 }
    val f2 = Future { Thread.sleep(800); 20 }
    val f3 = Future { Thread.sleep(500); 30 }

    // Combine all three using Future.sequence
    val combined: Future[String] = Future.sequence(List(f1, f2, f3)).map { results =>
      val sum = results.sum
      val avg = sum / results.size
      s"Sum = $sum, Average = $avg"
    }

    combined.onComplete {
      case Success(msg) => println(msg)
      case Failure(ex)  => println(s"Failed: $ex")
    }

    // Keep JVM alive long enough for demo (in scripts)
    Thread.sleep(1500)
}