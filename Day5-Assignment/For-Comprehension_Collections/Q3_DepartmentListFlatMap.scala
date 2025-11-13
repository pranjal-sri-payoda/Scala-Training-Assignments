object DepartmentListFlatMap {
  def main(args: Array[String]): Unit = {
    val departments = List(
      ("IT", List("Ravi", "Meena")),
      ("HR", List("Anita")),
      ("Finance", List("Vijay", "Kiran"))
    )

    // Using flatMap to get a flat list of all employees with their department names
    // val employees = departments.flatMap { case (dept, emps) =>
    //   emps.map(emp => s"$dept: $emp")
    // }


    def departmentEmployees(
        departments: List[(String, List[String])]
    ): List[String] = {

      val result =
        for {
          (dept, employees) <-
            departments         // pattern match tuple (department name, list of employees)
          emp <- employees      // flatten the inner employee list
        } yield s"$dept: $emp"  // produce formatted string

      result
    }

    println("List of all employees:")
    println(departmentEmployees(departments).mkString("\n"))
  }
}
