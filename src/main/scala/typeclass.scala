object typeclass extends App {

    trait ToBytes[T] {
        def toBytes(t: T): Array[Byte]
    }

    object ToBytes {
        implicit val StringHasBytes: ToBytes[String] = new ToBytes[String] {
            override def toBytes(string: String) = string.getBytes
        }
    }

    case class TwoStrings(a: String, b: String)
    object TwoStrings {
        implicit val ICanHazBytes: ToBytes[TwoStrings] = new ToBytes[TwoStrings] {
            override def toBytes(t: TwoStrings) = t.a.getBytes ++ t.b.getBytes
        }
    }

    def serialize[T](t: T)(implicit toBytes: ToBytes[T]): Array[Byte] = {
        toBytes.toBytes(t)
    }

    println(serialize("a string").toList)
    println(serialize(TwoStrings("two", "strings")).toList)

    import annotation.implicitNotFound

    @implicitNotFound(msg = "${T} is not allowed!")
    trait Allowed[T]

    object Allowed {
        implicit val IntIsAllowed = new Allowed[Int] {}
        implicit val StringIsAllowed = new Allowed[String] {}
    }

    def onlyIfAllowed[T: Allowed](t: T) {}

    onlyIfAllowed(2)
    onlyIfAllowed("boo")
//    onlyIfAllowed(Map(1 -> 2)) // does not compile

}
