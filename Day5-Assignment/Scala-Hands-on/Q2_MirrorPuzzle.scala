object MirrorPuzzle {
    def main(args: Array[String]): Unit = {
        def mirrorArray(arr: Array[Int]): Array[Int] = {
            var mirroredArr = new Array[Int](arr.length * 2)
            for (i <- arr.indices) {
                mirroredArr(i) = arr(i)
                mirroredArr(mirroredArr.length - 1 - i) = arr(i)
            }
            mirroredArr
        }
        
        val arr1 = Array(1, 2, 3, 4, 5)
        println(s"Original: ${arr1.mkString(", ")}\nMirrored: ${mirrorArray(arr1).mkString(", ")}\n")

        val arr2 = Array(3, 4, 1, 6, 9, 12, 2, 5)
        println(s"Original: ${arr2.mkString(", ")}\nMirrored: ${mirrorArray(arr2).mkString(", ")}")
    }
}