object SimpleRecursion {
  def main(args: Array[String]): Unit = {
    def digitSum(n: Int): Int = {
      if (n == 0) 0
      else (n % 10) + digitSum(n / 10)
    }

    println(s"The sum of digits of 1345 is ${digitSum(1345)}")
    println(s"The sum of digits of 74392 is ${digitSum(74392)}")
  }

}
