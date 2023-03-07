package mipt.monad.context

import cats.Monad
import mipt.monad.instances.{Reader, ReaderR}
import mipt.monad.transformers.{ReaderT, ReaderTF}

import mipt.monad.ApplicativeSyntax.*

trait Ask[F[_], E]:
  def ask: F[E]

object Ask:
  def apply[F[_], E](using Ask[F, E]): Ask[F, E] = summon[Ask[F, E]]
  
  given [R]: Ask[ReaderR[R], R] = new Ask[ReaderR[R], R]:
    override def ask: Reader[R, R] = identity

  given [F[_]: Monad, R]: Ask[ReaderTF[F, R], R] = new Ask[ReaderTF[F, R], R]:
    override def ask: ReaderT[F, R, R] = ReaderT(_.pure[F])
