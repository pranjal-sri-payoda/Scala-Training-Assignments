object FilteringOrdersAndTransformation {
  def main(args: Array[String]): Unit = {

    case class Order(id: Int, amount: Double, status: String)

    val orders = List(
      Order(1, 1200.0, "Delivered"),
      Order(2, 250.5, "Pending"),
      Order(3, 980.0, "Delivered"),
      Order(4, 75.0, "Cancelled")
    )

    def highAmountDeliveredOrders(orders: List[Order]): List[String] = {
        val filterOrders =
            for {
                order <- orders
                if order.amount > 500 && order.status == "Delivered"
            } yield s"Order #${order.id} -> ₹${order.amount}"

        filterOrders
    }

    val result = highAmountDeliveredOrders(orders)
    result.foreach(println)
  }
}
