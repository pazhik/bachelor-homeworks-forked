package mipt.monad.context

import mipt.monad.Monad
import mipt.monad.instances.{ReaderP, ReaderR}
import mipt.monad.transformers.{ReaderT, ReaderTF}

import mipt.monad.ApplicativeSyntax.*

trait Ask[F[_], E]:
  def ask: F[E]

object Ask:
  given [R]: Ask[ReaderR[R], R] = new Ask[ReaderR[R], R]:
    override def ask: ReaderP[R, R] = identity

  given [F[_]: Monad, R]: Ask[ReaderTF[F, R], R] = new Ask[ReaderTF[F, R], R]:
    override def ask: ReaderT[F, R, R] = ReaderT(_.pure[F])
