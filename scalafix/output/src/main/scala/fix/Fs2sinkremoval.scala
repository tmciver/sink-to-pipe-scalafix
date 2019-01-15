package fix

import fs2.Pipe

object Fs2sinkremoval {
  // Add code that needs fixing here.
  private def writeSink[F[_]]: Pipe[F, Byte, Unit] = ???
}
