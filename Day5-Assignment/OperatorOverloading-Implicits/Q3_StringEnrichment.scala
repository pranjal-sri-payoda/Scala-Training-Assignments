object StringEnrichment {
    implicit class RichString(val s: String) extends AnyVal {
        def *(n: Int): String =
            if (n <= 0) "" else {
                val sb = new StringBuilder(s.length * n)
                var i = 0
                while (i < n) { sb.append(s); i += 1 }
                sb.toString()
            }

        def ~(other: String): String = s + " " + other
    }

    def main(args: Array[String]): Unit = {
        // implicit enrichment is in scope here
        println("Hi" * 3)            // HiHiHi
        println("Hello" ~ "World")   // Hello World

    }
}