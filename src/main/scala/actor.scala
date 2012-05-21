import akka.actor._

object actors {

    case object WritesOff
    case class WriteToCluster(cluster: String)
    case class Write(data: String)

    class Master extends Actor {

        var clusterTarget: Option[String] = None

        def receive = {
            case WritesOff               => clusterTarget = None
            case WriteToCluster(cluster) => clusterTarget = Some(cluster)
            case Write(data)             => clusterTarget foreach (writeDataToCluster(data, _))
        }

        def writeDataToCluster(data: String, cluster: String) { /* ... */ }

    }

    object ZkClient {
        def watch(path: String)(callback: Option[String] => Unit) { /* ... */ }
    }

    val actor = ActorSystem("system").actorOf(Props(classOf[Master]))

    ZkClient.watch("/Status/writes") {
        _ match {
            case Some(cluster) => actor ! WriteToCluster(cluster)
            case None          => actor ! WritesOff
        }
    }

    actor ! Write("hello")

}
