package mipt.monad.context

import cats.{Monad, Monoid}
import mipt.monad.instances.{Writer, WriterW}
import mipt.monad.transformers.{WriterT, WriterTF}
import mipt.monad.ApplicativeSyntax.*

trait Tell[F[_], W]:
  def tell(log: W): F[Unit]

object Tell:
  def apply[F[_], W](using Tell[F, W]): Tell[F, W] = summon[Tell[F, W]]

  given [W: Monoid]: Tell[WriterW[W], W] = (log: W) => Writer(log, ())

  given [F[_]: Monad, W: Monoid]: Tell[WriterTF[F, W], W] = (log: W) => WriterT(Writer(log, ()).pure[F])

object TellSyntax:
  extension [W] (w: W)
    def tell[F[_]](using Tell[F, W]): F[Unit] = Tell[F, W].tell(w)
