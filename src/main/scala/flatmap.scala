object flatmap extends App {
    val keys = List("a", "b", "c")
    val map = Map("a" -> 2, "b" -> 3, "d" -> 4)

    // I want to return all pairs from map that are a key in keys.

    // One way
    val builder = List.newBuilder[(String, Int)]
    keys foreach { key =>
        if (map contains key) {
            val maybeValue = map get key
            val value = maybeValue.get // bad
            builder += (key -> value)
        }
    }
    println(builder.result)

    // Another way
    val okIfNotNegative = keys map { key =>
        key -> map.get(key).getOrElse(-1)
    } filter (_._2 != -1) // yuck
    println(okIfNotNegative)

    // with flatMap
    val results = keys flatMap { key =>
        map get key match {
            case Some(value) => Some(key -> value)
            case None => None
        }
    }
    println(results)
}
