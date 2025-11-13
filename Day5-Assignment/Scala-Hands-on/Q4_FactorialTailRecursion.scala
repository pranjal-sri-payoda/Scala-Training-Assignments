import scala.annotation.tailrec

object TailRecursion {
  def main(args: Array[String]): Unit = {

    def factorial(n: Int): Int = {
      println("Debug: Starting factorial computation") // Initial debug print
      @tailrec
      def helper(n: Int, acc: Int): Int = {
        println(s"[acc=$acc, n=$n]") // Debug print at each step
        if (n <= 1) acc
        else helper(n - 1, acc * n)
      }

      helper(n, 1)
    }

    println(s"Factorial of 7: ${factorial(7)}")
  }
}
