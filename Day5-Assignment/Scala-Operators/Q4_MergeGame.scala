@main
    def mergeGame(): Unit = {
        // Initial lists
        val list1 = List(1, 2)
        val list2 = List(3, 4)
        
        // ++ is a generic concatenation operator.
        // Works for any collections: List ++ List, List ++ Vector, Vector ++ List, etc.
        // Returns a collection of the same type as the left-hand side.

        // Merging the two lists using the ++ operator
        val merge1 = list1 ++ list2

        // ::: is specific to Lists.
        // Concatenates two lists only.

        // Merging the two lists using the ::: operator
        val merge2 = list1 ::: list2

        // Displaying the merged lists
        println(s"Merged Lists using '++' operator: $merge1")
        println(s"Merged Lists using ':::' operator: $merge1")


        val vec = Vector(5, 6)
        val merge3 = list1 ++ vec       
        println(s"Merged List and Vector using '++' operator: $merge3") //works

        // val merge4 = list2 ::: vec       
        // println(s"Merged List and Vector using ':::' operator: $merge4") //error

    }