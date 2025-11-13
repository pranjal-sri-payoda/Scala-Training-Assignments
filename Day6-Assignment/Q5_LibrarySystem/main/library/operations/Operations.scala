package library.operations

import library.items._
import library.users._

// Implicit default member
implicit val defaultMember: Member = new Member("Default Member")

// Borrow method using implicit member
def borrow(item: ItemType)(implicit member: Member): Unit = {
  member.borrowItem(item)
}

// Item description using pattern matching
def itemDescription(item: ItemType): Unit = item match {
  case Book(title)     => println(s"Book: $title")
  case Magazine(title) => println(s"Magazine: $title")
  case DVD(title)      => println(s"DVD: $title")
}

// Implicit conversion from String to Book
import scala.language.implicitConversions
implicit def stringToBook(title: String): Book = Book(title)
