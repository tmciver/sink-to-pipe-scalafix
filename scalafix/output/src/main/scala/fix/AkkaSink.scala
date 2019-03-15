/*
rule = Fs2sinkremoval
*/
package fix

import scala.concurrent.Future
import akka.Done
import akka.stream.scaladsl.Sink

object AkkaSink {
  val s: Sink[Int, Future[Done]] = ???
}
