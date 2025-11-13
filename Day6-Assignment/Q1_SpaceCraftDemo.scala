abstract class Spacecraft(val fuelLevel: Int) {
    def launch(): Unit      // abstract method

    def land(): Unit = println(s"${this.getClass.getSimpleName} performing standard landing sequence. FuelLevel=$fuelLevel") // concrete method
}

// Optional Trait: can be mixed into any spacecraft
trait Autopilot {
    def autoNavigate(): Unit = println(s"${this.getClass.getSimpleName} autopilot: engaging default auto-navigation.")
}

// CargoShip implements launch, can optionally override land()
class CargoShip(fuel: Int) extends Spacecraft(fuel) {
    override def launch(): Unit =                                                   // overriding abstract method
        println(s"CargoShip launching heavy cargo. FuelLevel=$fuelLevel")

    override def land(): Unit =                                                     // overriding concrete method 
        println(s"CargoShip landing slowly due to cargo. FuelLevel=$fuelLevel")
}

// PassengerShip implements launch and provides a final land()
class PassengerShip(fuel: Int) extends Spacecraft(fuel) {
    override def launch(): Unit =                                                   // overriding abstract method
        println(s"PassengerShip launching with passengers on board. FuelLevel=$fuelLevel")

    final override def land(): Unit =                                               // overriding concrete method with final keyword to stop further overrides
        println(s"PassengerShip landing with passenger safety protocols. FuelLevel=$fuelLevel")
}

// LuxuryCruiser is final: cannot be subclassed further
final class LuxuryCruiser(fuel: Int) extends PassengerShip(fuel) {
    override def launch(): Unit =
        println(s"LuxuryCruiser launching in luxury mode. FuelLevel=$fuelLevel")

    def playEntertainment(): Unit =
        println("LuxuryCruiser: playing premium entertainment for passengers.")
}


// Demonstration
@main def SpaceCraftDemo(): Unit = {
    
    // CargoShip instance mixing in Autopilot (uses default autoNavigate)
    val cargo = new CargoShip(85) with Autopilot
    cargo.launch()
    cargo.autoNavigate()
    cargo.land()

    println("---")

    // PassengerShip instance mixing in Autopilot and overriding autoNavigate inline
    val passenger = new PassengerShip(70) with Autopilot {
        override def autoNavigate(): Unit =
            println(s"${this.getClass.getSimpleName} autopilot: smooth cruise mode engaged.")
    }
    passenger.launch()
    passenger.autoNavigate()
    passenger.land()

    println("---")

    // LuxuryCruiser instance (final class) - cannot be subclassed
    // val cruiser = new LuxuryCruiser(100) with Autopilot // This would fail if we tried to mix in Autopilot
    val cruiser = new LuxuryCruiser(100)
    cruiser.launch()
    cruiser.playEntertainment()
    cruiser.land()

    // cruiser.autoNavigate()   // This will not compile: LuxuryCruiser does not mix in Autopilot

    println("---")
}
