package mipt.monad.transformers

import cats.Monad
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.MonadSyntax.*

import scala.annotation.tailrec

case class EitherT[F[_]: Monad, E, A](value: F[Either[E, A]])
type EitherTF[F[_], E] = [A] =>> EitherT[F, E, A]

object EitherT:
  given [F[_]: Monad, E]: Monad[EitherTF[F, E]] = new Monad[EitherTF[F, E]]:
    override def pure[A](a: A): EitherT[F, E, A] = EitherT(Right(a).pure[F])

    override def flatMap[A, B](fa: EitherT[F, E, A])(f: A => EitherT[F, E, B]): EitherT[F, E, B] =
      EitherT(fa.value.flatMap(_ match
        case Left(e)  => Left(e).pure[F]
        case Right(a) => f(a).value
      ))

    override def tailRecM[A, B](a: A)(f: A => EitherT[F, E, Either[A, B]]): EitherT[F, E, B] =
      EitherT(
        f(a).value.flatMap(_ match
          case Left(e)         => Left(e).pure[F]
          case Right(Left(a))  => tailRecM(a)(f).value
          case Right(Right(b)) => Right(b).pure[F]
        )
      )
