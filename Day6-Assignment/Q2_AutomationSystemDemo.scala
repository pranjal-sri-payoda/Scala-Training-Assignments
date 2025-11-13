trait Device {
  def turnOn(): Unit           // abstract
  def turnOff(): Unit          // abstract
  def status(): Unit = println("Device is operational")  // concrete
}

trait Connectivity {
  def connect(): Unit = println("Device connected to network")
  def disconnect(): Unit = println("Device disconnected")
}

trait EnergySaver {
  def activateEnergySaver(): Unit = println("Energy saver mode activated")
  def turnOff(): Unit = println("Device powered down to save energy") // overrides Device.turnOff
}

class SmartLight extends Device with Connectivity with EnergySaver {
  override def turnOn(): Unit = println("SmartLight turned on")
  // turnOff() comes from EnergySaver due to trait resolution order
}

// Class representing a smart thermostat
class SmartThermostat extends Device with Connectivity {
  override def turnOn(): Unit = println("SmartThermostat turned on")
  override def turnOff(): Unit = println("SmartThermostat turned off")
}

// optional trait
trait VoiceControl {
  def turnOn(): Unit = println("Voice command: Turn on device")
  def turnOff(): Unit = println("Voice command: Turn off device")
}

@main def AutomationSystemDemo(): Unit = {
  val light = new SmartLight
  light.turnOn()               // SmartLight turned on
  light.turnOff()              // Device powered down to save energy (EnergySaver)
  light.status()               // Device is operational
  light.connect()              // Device connected to network
  light.activateEnergySaver()  // Energy saver mode activated

  println("-----")

  val thermostat = new SmartThermostat
  thermostat.turnOn()          // SmartThermostat turned on
  thermostat.turnOff()         // SmartThermostat turned off
  thermostat.status()          // Device is operational
  thermostat.connect()         // Device connected to network

  println("-----")

    //   Mixing VoiceControl into SmartLight
    //   val voiceLight = new SmartLight with VoiceControl

    // SmartLight already overrides turnOn() (from Device), and VoiceControl also defines turnOn(). 
    // When you mix both, Scala doesn't know which to use. Resolve by explicitly overriding
    
    //   voiceLight.turnOn()           
    //   voiceLight.turnOff()         // Voice command: Turn off device

    val voiceLight = new SmartLight with VoiceControl {
        override def turnOn(): Unit = super[VoiceControl].turnOn()
        override def turnOff(): Unit = super[VoiceControl].turnOff()
    }
    voiceLight.turnOn()          // Voice command: Turn on device
    voiceLight.turnOff()         // Voice command: Turn off device
    
}
