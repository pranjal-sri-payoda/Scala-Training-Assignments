package model

case class WaterReading(
                         meter_id: String,
                         household_id: Int,
                         consumption_liters: Double,
                         pressure: Double,
                         device_status: String,
                         timestamp: Long
                       )
