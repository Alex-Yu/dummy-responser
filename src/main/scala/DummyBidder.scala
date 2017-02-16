import akka.actor.ActorSystem
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import spray.http.StatusCodes
import spray.routing.SimpleRoutingApp

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Random, Try}
import scala.concurrent.ExecutionContext.Implicits.global

object DummyBidder extends App {
  val qtty = Try {
    args(0).toInt
  }.getOrElse(3)
  val share = Try {
    args(1).toInt
  }.getOrElse(5)
  val delay = Try {
    args(2).toInt
  }.toOption

 /* println("Enter number of virtual bidders")
  val count = StdIn.readInt()
  require(count > 0, "Must be positive")*/

  for (i <- 0 until qtty) new DummyBidder(share, delay).start(8090 + i)

}



class DummyBidder(val share: Int, val delay: Option[Int]) extends SimpleRoutingApp  {
  println(s"Started with $share% response and ${if (delay.isDefined) s"${delay.get}ms" else "no"} delay")

  implicit val system = ActorSystem("my-system")
  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val r = Random
  val bidResponse = BidderUtil.readFile("bid_response_real_2.json")

  def getPrice = (r.nextFloat() + 1) * 100  // +1 to avoid bids less than floor

  def getDelay = r.nextInt(400) + 100

  def hasBid(share: Int) =
    share > 0 && r.nextInt(101) <= share

  def in[U](duration: FiniteDuration)(body: => U): Unit =
    system.scheduler.scheduleOnce(duration)(body)

  def start(port: Int) = {
    startServer(interface = "0.0.0.0", port = port) {
      path("bidresponse") {
        post { ctx =>
          val currentDelay = delay.getOrElse(getDelay)
          in(currentDelay millisecond) {
            ctx.complete {
              logger.debug(s"<=== ${ctx.request.entity.asString}")
            
              if (hasBid(share)) {
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
