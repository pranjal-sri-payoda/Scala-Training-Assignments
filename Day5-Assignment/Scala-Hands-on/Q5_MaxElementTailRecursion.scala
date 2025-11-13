import scala.annotation.tailrec

object MaxElementTailRecursion {
    def main(args: Array[String]): Unit = {
        val numbers = Array(5, 9, 3, 7, 2)
        val maxElement = findMax(numbers)
        println(s"The maximum element in the array is: $maxElement")
    }

    def findMax(arr: Array[Int]): Int = {
        if (arr.isEmpty) throw new NoSuchElementException("Array is empty")

        @tailrec
        def helper(index: Int, currentMax: Int): Int = {
            if (index >= arr.length) currentMax
            else {
                val newMax = if (arr(index) > currentMax) arr(index) else currentMax
                helper(index + 1, newMax)
            }
        }

        helper(1, arr(0))
    }
}