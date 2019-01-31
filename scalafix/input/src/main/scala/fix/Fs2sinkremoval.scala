/*
rule = Fs2sinkremoval
*/
package fix

import fs2.{Sink, Stream}

object Fs2sinkremoval {

  private def foo[F[_], A](s: Stream[F, A], sink: Sink[F, A]): Stream[F, Unit] =
    s.to(sink)
}
