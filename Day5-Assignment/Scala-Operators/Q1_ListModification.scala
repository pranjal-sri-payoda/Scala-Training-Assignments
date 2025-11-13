object ListModification {
    def main(args: Array[String]): Unit = {

        //Since Lists are immutable, we need to reassign the variable to modify it
        //If we use val, it will give a compilation error while adding at the beginning or end
        var numbers = List(2, 4, 6)

        //Adding 8 in the last of the list
        numbers = numbers :+ 8

        //Adding 0 at the beginning of the list
        numbers = 0 +: numbers

        println(numbers)
    }
}