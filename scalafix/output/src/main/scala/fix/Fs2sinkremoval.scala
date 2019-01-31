package fix

import fs2.{Pipe, Pure}

object Fs2sinkremoval {
  private def writeSink[F[_]]: Pipe[F, Byte, Unit] = ???
}
