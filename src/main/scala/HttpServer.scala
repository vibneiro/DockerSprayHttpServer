import akka.actor.{ActorSystem}
import spray.routing._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object HttpServer extends App with SimpleRoutingApp {

  implicit val actorSystem = ActorSystem()
  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  startServer(interface = "localhost", port = 8080) {

    // GET /welcome --> "Welcome!" response
    get {
      path("welcome") {
        complete {
          <html>
            <h1>Welcome!</h1>
            <p><a href="/terminate?method=post">Stop the server</a></p>
          </html>
        }
      }
    } ~
      // POST /terminate --> "The server is stopping" response
      (post | parameter('method ! "post")) {
        path("terminate") {
          complete {
            actorSystem.scheduler.scheduleOnce(1.second)(actorSystem.shutdown())(actorSystem.dispatcher)
            "The server is stopping"
          }
        }
      }
  }
    .onComplete {
    case Success(b) =>
      println("Successfully started")
    case Failure(ex) =>
      println(ex.getMessage)
      actorSystem.shutdown()
  }
}
