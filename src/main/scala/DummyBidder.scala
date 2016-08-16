import akka.actor.ActorSystem
import spray.http.{StatusCode, StatusCodes}
import spray.routing.SimpleRoutingApp
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply

object DummyBidder {
  def main(args: Array[String]): Unit = {
    new DummyBidder().start(8090, 5.5f)
  }
}

class DummyBidder extends SimpleRoutingApp {
  implicit val system = ActorSystem("my-system")

  val bidResponse = BidderUtil.readFile("bid_response.json")

  def start(port: Int, price: Float) = {
    startServer(interface = "0.0.0.0", port = port) {
      path("bidresponse") {
        post {
          complete {
            println(s"Get request!!!")
            bidResponse.replaceAll("\\$price", price.toString)
          }
        }
      }
    }
  }

  def shutdown() = {
    system.shutdown()
    system.awaitTermination()
  }
}
