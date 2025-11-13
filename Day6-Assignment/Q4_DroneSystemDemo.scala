trait Drone {
    def activate(): Unit        // abstract method
    def deactivate(): Unit      // abstract method

    def status(): Unit = {        // concrete method
        println("Drone is operational.")
    }
}

trait NavigationModule {
  def flyTo(destination: String): Unit = println(s"Flying to $destination")
  def deactivate(): Unit = println("Navigation systems shutting down") 
}

trait DefenseModule {
  def activateShields(): Unit = println("Shields activated")
  def deactivate(): Unit = println("Defense systems deactivated")
}

trait CommunicationModule {
  def sendMessage(msg: String): Unit = println(s"Sending message: $msg")
  def deactivate(): Unit = println("Communication module shutting down") // optional override
}

class BasicDrone extends Drone {
  override def activate(): Unit = println("BasicDrone activated")
  override def deactivate(): Unit = println("BasicDrone deactivated")
}

object DroneFleetTest extends App {

    // Multiple traits define deactivate(), creating conflicts. 
    // Scala can't auto-resolve trait-to-trait conflicts without explicit override.

  println("----- Drone 1: Navigation + Defense -----")
  val drone1 = new BasicDrone with NavigationModule with DefenseModule {
    override def deactivate(): Unit = super[DefenseModule].deactivate()
  }
  drone1.activate()
  drone1.flyTo("Mars")
  drone1.activateShields()
  drone1.status()
  drone1.deactivate()

  println("----- Drone 2: Communication + Navigation -----")
  val drone2 = new BasicDrone with CommunicationModule with NavigationModule {
    override def deactivate(): Unit = super[NavigationModule].deactivate()
  }
  drone2.activate()
  drone2.sendMessage("Hello Galaxy!")
  drone2.flyTo("Jupiter")
  drone2.deactivate()

  println("----- Drone 3: All modules -----")
  val drone3 = new BasicDrone with DefenseModule with CommunicationModule with NavigationModule {
    override def deactivate(): Unit = super[NavigationModule].deactivate()
  }
  drone3.activate()
  drone3.activateShields()
  drone3.sendMessage("All systems go!")
  drone3.flyTo("Saturn")
  drone3.deactivate()

  println("----- Drone 4: Different order -----")
  val drone4 = new BasicDrone with NavigationModule with CommunicationModule with DefenseModule {
    override def deactivate(): Unit = super[DefenseModule].deactivate()
  }
  drone4.activate()
  drone4.flyTo("Venus")
  drone4.sendMessage("Mission ready")
  drone4.activateShields()
  drone4.deactivate()
}