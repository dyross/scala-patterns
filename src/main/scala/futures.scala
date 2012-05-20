object futures {

    import akka.actor._
    import akka.dispatch._
    import akka.util.duration._

    implicit val system = ActorSystem("foo")
    implicit val ec = ExecutionContext.defaultExecutionContext

    def a: Future[String] = Future { "a" }
    def b: Future[Int] = Future { 2 }
    def c: Future[List[Int]] = Future { Nil }

    case class ABC(a: String, b: Int, c: List[Int])

    // how to write a funciton to return an ABC from those above?

    def blockingAndNotTypeSafe: ABC = {
        val futureA = a
        val futureB = b
        val futureC = c

        val bigFuture: Future[List[_]] = Future sequence List(futureA, futureB, futureC)

        val results = Await.result(bigFuture, 10 seconds)

        ABC(
            a = results(0).asInstanceOf[String], 
            b = results(1).asInstanceOf[Int],
            c = results(2).asInstanceOf[List[Int]])
    }

    def nonBlockingAndAmazing: Future[ABC] = {
        val futureA = a
        val futureB = b
        val futureC = c

        for (nowA <- futureA; nowB <- futureB; nowC <- futureC) yield ABC(nowA, nowB, nowC)
    }

    def onlyReturnIfBIs2: Future[Option[ABC]] = {
        val abcFuture = nonBlockingAndAmazing

        abcFuture map { abc =>
            if (abc.b == 2) Some(abc) else None
        }
    }

    // Really slow
    def getScoreFromHBase(kloutId: String) = Future(100.0)
    def getNameFromMySQL(klout: String) = Future("david")

    case class Profile(score: Double, name: String)

    def buildProfileWithScoreAndName(score: Double, name: String) =
        Profile(score, name)

    def blockingUgly: Profile = {
        val scoreFuture = getScoreFromHBase("1")
        val nameFuture = getNameFromMySQL("1")
        val futureList: Future[List[Any]] =
            Future sequence List(scoreFuture, nameFuture)
        val results = Await result (futureList, 10 seconds) 
        Profile(results(0).asInstanceOf[Double],
                results(1).asInstanceOf[String])
    }

    def nonblockingPretty: Future[Profile] = {
        val scoreFuture = getScoreFromHBase("1")
        val nameFuture = getNameFromMySQL("1")
        for (score <- scoreFuture; name <- nameFuture)
            yield Profile(score, name) 
    }

}
