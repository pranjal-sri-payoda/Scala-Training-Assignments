import akka.actor.typed.ActorSystem
import bootstrap.RootBehavior

object Main {
  def main(args: Array[String]): Unit = {
    ActorSystem(RootBehavior(), "VisitorConsumerSystem")
  }
}
