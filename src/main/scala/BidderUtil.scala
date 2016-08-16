import scala.io.Source

object BidderUtil {

  def readFile(file: String): String = {
    Source.fromInputStream(Thread.currentThread().getContextClassLoader.getResourceAsStream(file)).mkString
  }

}
