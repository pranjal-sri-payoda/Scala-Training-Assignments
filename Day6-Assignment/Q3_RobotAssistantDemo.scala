// Base trait for any robot
trait Robot {
  def start(): Unit // abstract
  def shutdown(): Unit // abstract
  def status(): Unit = println("Robot is operational") // concrete
}

// Trait for speech capabilities
trait SpeechModule {
  def speak(message: String): Unit = println(s"Robot says: $message")
}

// Trait for movement capabilities
trait MovementModule {
  def moveForward(): Unit = println("Moving forward")
  def moveBackward(): Unit = println("Moving backward")
}

// Trait for energy-saving behavior
trait EnergySaver {
  def activateEnergySaver(): Unit = println("Energy saver mode activated")
  def shutdown(): Unit = println(
    "Robot shutting down to save energy"
  ) // overrides Robot.shutdown
}

// Base class
class BasicRobot extends Robot {
  override def start(): Unit = println("BasicRobot starting up")
  override def shutdown(): Unit = println("BasicRobot shutting down")
}

// Testing per-instance robot customization
@main def RobotAssistantDemo(): Unit = {

  println("----- Robot 1: Speech + Movement -----")
  val robot1 = new BasicRobot with SpeechModule with MovementModule
  robot1.start() // BasicRobot starting up
  robot1.status() // Robot is operational
  robot1.speak("Hello!") // Robot says: Hello!
  robot1.moveForward() // Moving forward
  robot1.shutdown() // BasicRobot shutting down (BasicRobot method)

  // Scala can use linearization when traits don't conflict with a concrete class method override.
  // The issue here is that BasicRobot (a class) explicitly overrides shutdown(),
  // which creates an ambiguity that linearization alone can't resolve.

  println("----- Robot 2: EnergySaver + Movement -----")
//   val robot2 = new BasicRobot with EnergySaver with MovementModule
  val robot2 = new BasicRobot with EnergySaver with MovementModule {
    override def shutdown(): Unit =
      super[BasicRobot].shutdown() // Explicitly choose EnergySaver's shutdown
  }
  robot2.start() // BasicRobot starting up
  robot2.moveBackward() // Moving backward
  robot2.activateEnergySaver() // Energy saver mode activated
  robot2.shutdown() // Robot shutting down to save energy (EnergySaver method)

  println("----- Robot 3: Movement + EnergySaver -----")
  // val robot3 = new BasicRobot with MovementModule with EnergySaver

  val robot3 = new BasicRobot with MovementModule with EnergySaver {
    override def shutdown(): Unit =
      super[EnergySaver].shutdown() // Explicitly choose EnergySaver's shutdown
  }
  robot3.start() // BasicRobot starting up
  robot3.moveForward() // Moving forward
  robot3.activateEnergySaver() // Energy saver mode activated
  robot3.shutdown() // Robot shutting down to save energy (EnergySaver method due to last trait wins)
}
