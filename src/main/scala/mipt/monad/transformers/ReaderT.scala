package mipt.monad.transformers

import cats.Monad
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

import scala.annotation.tailrec

case class ReaderT[F[_], R, A](value: R => F[A])
type ReaderTF[F[_], R] = [A] =>> ReaderT[F, R, A]

object ReaderT:
  given [F[_]: Monad, R]: Monad[ReaderTF[F, R]] = new Monad[ReaderTF[F, R]]:
    override def pure[A](a: A): ReaderT[F, R, A] = ReaderT(_ => a.pure[F])

    override def flatMap[A, B](fa: ReaderT[F, R, A])(f: A => ReaderT[F, R, B]): ReaderT[F, R, B] =
      ReaderT(r => for {
        a <- fa.value(r)
        b <- f(a).value(r)
      } yield b)

    override def tailRecM[A, B](a: A)(f: A => ReaderT[F, R, Either[A, B]]): ReaderT[F, R, B] =
      ReaderT(r => Monad[F].tailRecM(a)(a => f(a).value(r)))
