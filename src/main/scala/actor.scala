import akka.actor._
import akka.routing.RoundRobinRouter

object actors {

    case object WritesOff
    case class WriteToCluster(cluster: String)
    case class Write(data: String)
    case class DataAndCluster(data: String, cluster: String)

    class Master extends Actor {

        var clusterTarget: Option[String] = None

        val writeRouter = context actorOf
            (Props[Writer].withRouter(RoundRobinRouter(nrOfInstances = 5)))

        def receive = {
            case WritesOff               => clusterTarget = None
            case WriteToCluster(cluster) => clusterTarget = Some(cluster)
            case Write(data)             => clusterTarget foreach (writeRouter ! DataAndCluster(data, _))
        }
    }

    class Writer extends Actor {

        def receive = {
            case DataAndCluster(data: String, cluster: String) => // ...
        }

    }

    object ZkClient {
        def watch(path: String)(callback: Option[String] => Unit) { /* ... */ }
    }

    val actor = ActorSystem("system") actorOf Props[Master] 

    ZkClient.watch("/Status/writes") {
        _ match {
            case Some(cluster) => actor ! WriteToCluster(cluster)
            case None          => actor ! WritesOff
        }
    }

    actor ! Write("hello")

}
