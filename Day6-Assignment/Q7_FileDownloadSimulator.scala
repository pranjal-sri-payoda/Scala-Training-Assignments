class DownloadTask(fileName: String, downloadSpeed: Int) extends Thread {

  override def run(): Unit = {
    try {
      for (progress <- 10 to 100 by 10) {
        Thread.sleep(downloadSpeed) // simulate time delay per 10%
        println(s"$fileName: $progress% downloaded")
      }
      println(s"$fileName download completed!")
    } catch {
      case e: InterruptedException =>
        println(s"$fileName download interrupted: ${e.getMessage}")
    }
  }
}

object FileDownloadSimulator {
  def main(args: Array[String]): Unit = {
    println("Starting multiple file downloads...\n")

    // Create download threads with different speeds (milliseconds per 10%)
    val fileA = new DownloadTask("FileA.zip", 500)   // slower
    val fileB = new DownloadTask("FileB.mp4", 300)   // medium
    val fileC = new DownloadTask("FileC.pdf", 200)   // faster

    // Start all downloads concurrently
    fileA.start()
    fileB.start()
    fileC.start()

    fileA.join()
    fileB.join()
    fileC.join()

    println("\nAll downloads completed!")
  }
}
