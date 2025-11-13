object MultiplicationTable {
    def main(args: Array[String]): Unit = {
        def multiplicationTable(n: Int): List[String] = {
            var table = 
                for {
                        i <- 1 to n
                        j <- 1 to n
                } yield s"$i x $j = ${i * j}"
            table.toList
        }

        println("Multiplication Table: ")
        println(s"${multiplicationTable(3).mkString("\n")}")
    }
}