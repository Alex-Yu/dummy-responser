import akka.actor.ActorSystem
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import spray.http.StatusCodes
import spray.routing.Directive.pimpApply
import spray.routing.SimpleRoutingApp
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

object DummyBidder extends App {

  println("Enter number of virtual bidders")
  val count = StdIn.readInt()
  require(count > 0, "Must be positive")

  for (i <- 0 until count) new DummyBidder().start(8090 + i)

}



class DummyBidder extends SimpleRoutingApp  {
  implicit val system = ActorSystem("my-system")
  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val r = Random
  val bidResponse = BidderUtil.readFile("bid_response_real.json")
  def getPrice = r.nextFloat() * 100
  def getDelay = r.nextInt(400) + 100

  def in[U](duration: FiniteDuration)(body: => U): Unit =
    system.scheduler.scheduleOnce(duration)(body)

  def start(port: Int) = {
    startServer(interface = "0.0.0.0", port = port) {
      path("bidresponse") {
        post { ctx =>
          val delay = getDelay
          in(delay millisecond) {
            ctx.complete {
              logger.debug(s"<=== ${ctx.request.entity.asString}")

              if (r.nextBoolean()) {
                val result = bidResponse.replaceAll("\\$price", getPrice.toString)
                logger.debug(s"===> after $delay ms: $result")
                result
              }
              else {
                respondWithStatus(StatusCodes.NoContent)
                logger.debug(s"===> after $delay ms: NoContent")
                ""
              }
            }
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
