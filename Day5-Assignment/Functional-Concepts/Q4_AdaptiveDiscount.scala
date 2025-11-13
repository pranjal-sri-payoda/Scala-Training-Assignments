object AdaptiveDiscount {
    def discountStrategy(memberType: String): Double => Double =
        memberType.trim.toLowerCase match {
            case "gold"   => price => price - (price * 0.2)
            case "silver" => price => price - (price * 0.1)
            case _        => price => price
        }

    def main(args: Array[String]): Unit = {
        val goldDiscount = discountStrategy("gold")
        println(s"Gold member price for 1000: ${goldDiscount(1000)}")

        val silverDiscount = discountStrategy("silver")
        println(s"Silver member price for 1000: ${silverDiscount(1000)}")

        val noDiscount = discountStrategy("bronze")
        println(s"Non-member price for 1000(No discount): ${noDiscount(1000)}") 
    }
}