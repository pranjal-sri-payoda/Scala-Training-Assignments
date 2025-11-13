import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._

object DependentFuturesDemo{

  def getUser(id: Int): Future[String] = Future {
    println(s"Fetching user $id")
    s"User$id"
  }

  def getOrders(user: String): Future[List[String]] = Future {
    println(s"Fetching orders for $user")
    List(s"$user-order1", s"$user-order2")
  }

  def getOrderTotal(order: String): Future[Double] = Future {
    val total = scala.util.Random.between(10.0, 100.0)
    println(f"Computing total for $order: $total%.3f")
    total
  }

  @main def runDependentFutures(): Unit =

    val pipeline: Future[Double] = for {
      user   <- getUser(42)
      orders <- getOrders(user)
      totals <- Future.sequence(orders.map(getOrderTotal)) // sequence List[Future[Double]] to Future[List[Double]]
    } yield totals.sum

    pipeline.onComplete {
      case Success(total) => println(f"Total amount: $total%.3f")
      case Failure(ex)    => println(s"Failed: $ex")
    }

    // Wait for the pipeline to complete (necessary in scripts)
    Await.result(pipeline, 5.seconds)
}