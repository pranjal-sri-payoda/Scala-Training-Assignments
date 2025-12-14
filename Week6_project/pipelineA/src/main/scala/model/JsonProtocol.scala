package model

import io.circe.syntax._
import io.circe.generic.auto._

object JsonProtocol {
  def toJson(reading: WaterReading): String =
    reading.asJson.noSpaces
}
