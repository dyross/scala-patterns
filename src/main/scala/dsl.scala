object dsl extends App {

    case class RepeatableInt(int: Int) {
        def times = new {
            def repeat(f: => Unit) {
                (1 to int) foreach (_ => f)
            }
        }
    }

    (RepeatableInt(10) times) repeat {
        println("this is lame")
    }

    implicit def IntsAreRepeatable(int: Int): RepeatableInt = RepeatableInt(int)

    5.times.repeat {
        println("much better")
    }

}
