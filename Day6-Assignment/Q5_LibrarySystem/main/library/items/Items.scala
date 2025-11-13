package library.items

// Sealed trait for restricted hierarchy
sealed trait ItemType {
  def title: String
}

// Subclasses
case class Book(title: String) extends ItemType
case class Magazine(title: String) extends ItemType
case class DVD(title: String) extends ItemType
