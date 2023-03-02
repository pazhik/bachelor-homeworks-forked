package mipt.monad.context

import mipt.auxiliary.MonoidP
import mipt.monad.Monad
import mipt.monad.instances.{WriterP, WriterW}
import mipt.monad.transformers.{WriterT, WriterTF}

import mipt.monad.ApplicativeSyntax.*

trait Tell[F[_], W]:
  def tell(log: W): F[Unit]

object Tell:
  given [W: MonoidP]: Tell[WriterW[W], W] = (log: W) => WriterP(log, ())

  given [F[_]: Monad, W: MonoidP]: Tell[WriterTF[F, W], W] = (log: W) => WriterT(WriterP(log, ()).pure[F])
