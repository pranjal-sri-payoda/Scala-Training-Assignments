package model

import org.apache.spark.sql.types._

object ReadingSchema {

  val schema: StructType = new StructType()
    .add("meter_id", StringType)
    .add("household_id", IntegerType)
    .add("consumption_liters", DoubleType)
    .add("pressure", DoubleType)
    .add("device_status", StringType)
    .add("timestamp", LongType)
    .add("timestamp_readable", StringType, true)
}
