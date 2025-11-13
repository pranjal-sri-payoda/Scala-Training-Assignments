// Internal working of the `if` inside the for-coprehension is to filter out the pairs 
// where the student's name length is greater than the subject's name length.

// students.flatMap(student =>
//   subjects.withFilter(subject => student.length > subject.length)
//           .map(subject => (student, subject))
// )


object ListCartesianAndFilter {
    def main(args: Array[String]): Unit = {
        def studentSubjectPairs(students: List[String], subjects: List[String]): List[(String, String)] = {
            for {
                s <- students
                sub <- subjects
                if s.length > sub.length
            } yield (s, sub)
        }

        val students = List("Asha", "Bala", "Chitra")
        val subjects = List("Math", "Physics")

        val result = studentSubjectPairs(students, subjects)
        println(result) // Output: List((Asha,Math)) (correct output) --- the output in the assignment pdf is wrong
    }
}