import scalaz._
import Scalaz._
import akka.actor._
import akka.dispatch._
import akka.util.duration._
import java.util.Date

object demo extends App {

    import setup._

    // Say we have these computations that return futures...
    def fa = Future(waitAndReturn(1))
    def fb = Future(waitAndReturn(2))
    def fc = Future(waitAndReturn(3))

    // And want to combine them with this pure function...
    val foo: (Int, Int, Int) => Int = (a, b, c) => a + b + c

    // The typical way would be to do this...
    // It takes ~300 ms.
    time("monad style") {
        val result = for (a <- fa; b <- fb; c <- fc) yield foo(a, b, c)
        await(result)
    }

    // If we want them all to be parallel, we can do it this (annoying) way...
    // It takes ~100 ms.
    time("annoying monad style") {
        val ffa = fa
        val ffb = fb
        val ffc = fc
        val result = for (a <- ffa; b <- ffb; c <- ffc) yield foo(a, b, c) 
        await(result)
    }

    // With scalaz, we can do this instead...
    // It takes ~ 200 ms. WTF.
    time("applicative style") {
        val result = (fa |@| fb |@| fc) apply foo
        await(result)
    }

    // Allow the thing to stop...
    system.shutdown()
}

object setup {

    val system = ActorSystem("fun")
    implicit val ec: ExecutionContext = ExecutionContext.defaultExecutionContext(system)

    def waitAndReturn[A](a: A): A = {
        Thread.sleep(100)
        a
    }

    def time[A](name: String)(a: => A): A = {
        val start = new Date
        val result = a
        val end = new Date
        val took = end.getTime - start.getTime
        println("name [%s] result [%s] took [%d]".format(name, result, took))
        result
    }

    def await[A](future: Future[A]): A = Await.result(future, 30 seconds)

    implicit val futureFunctor: Functor[Future] = new Functor[Future] {
        override def fmap[A, B](future: Future[A], f: A => B): Future[B] = future map f
    }

    implicit val futurePure: Pure[Future] = new Pure[Future] {
        override def pure[A](a: => A): Future[A] = Future(a)
    }

    implicit val futureApply: Apply[Future] = new Apply[Future] {
        override def apply[A, B](futureF: Future[A => B], futureA: Future[A]): Future[B] = {
            for (f <- futureF; a <- futureA) yield f(a)
        }
    }

}
