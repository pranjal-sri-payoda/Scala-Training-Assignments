package main

import library.items._
import library.users._
import library.operations._

object LibraryMain extends App {

  // Explicit member
  val alice = new Member("Alice")
  val book1: Book = Book("Scala Programming")
  borrow(book1)(using alice)          // Alice borrows 'Scala Programming'

  println("-----")

  // Using implicit default member
  val dvd1: DVD = DVD("Inception")
  borrow(dvd1)(using new Member("Pranjal"))                  // Created new Member borrows 'Inception'

  println("-----")

  // Using implicit conversion from String to Book
  borrow("Harry Potter")        // Default Member borrows 'Harry Potter'

  println("-----")

  // Demonstrate sealed trait pattern matching
  val items: List[ItemType] = List(
    Book("FP in Scala"),
    Magazine("Science Today"),
    DVD("Matrix")
  )
  items.foreach(itemDescription)
}
