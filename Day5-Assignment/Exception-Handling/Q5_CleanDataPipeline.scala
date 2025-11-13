import scala.util.Try

object DataPipelineDemo {

  @main def runPipeline(): Unit = {
    val data = List("10", "20", "x", "30")

    val result: List[Int] = data.flatMap(s =>
      Try(s.toInt).toOption  
    ).map(n => n * n)        

    println(result)  
  }
}
