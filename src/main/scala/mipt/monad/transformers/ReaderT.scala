package mipt.monad.transformers

import mipt.monad.Monad

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.MonadSyntax.*

case class ReaderT[F[_]: Monad, R, A](value: R => F[A])
type ReaderTF[F[_], R] = [A] =>> ReaderT[F, R, A]

object ReaderT:
  given [F[_]: Monad, R]: Monad[ReaderTF[F, R]] = new Monad[ReaderTF[F, R]]:
    override def pure[A](a: A): ReaderT[F, R, A] = ReaderT(_ => a.pure[F])

    override def flatMap[A, B](fa: ReaderT[F, R, A])(f: A => ReaderT[F, R, B]): ReaderT[F, R, B] =
      ReaderT((r: R) => fa.value(r).flatMap(a => f(a).value(r)))
