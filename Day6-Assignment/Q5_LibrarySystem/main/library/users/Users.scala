package library.users

import library.items.ItemType

// Member class
class Member(val name: String) {
  def borrowItem(item: ItemType): Unit = {
    println(s"$name borrows '${item.title}'")
  }
}
