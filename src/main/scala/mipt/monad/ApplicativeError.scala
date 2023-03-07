package mipt.monad

import cats.{Applicative, Monad}
import mipt.monad.instances.EitherE
import mipt.monad.transformers.{EitherT, EitherTF}
import mipt.monad.transformers.EitherT.*

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.MonadSyntax.*

trait ApplicativeError[F[_], E] extends Applicative[F] {
  def raiseError[A](e: E): F[A]
  def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]
  def handleError[A](fa: F[A])(f: E => A): F[A]
  def attempt[A](fa: F[A]): F[Either[E, A]]
}

object ApplicativeError:
  def apply[F[_], E](using ApplicativeError[F, E]): ApplicativeError[F, E] = summon[ApplicativeError[F, E]]
  
  given [E]: ApplicativeError[EitherE[E], E] = new ApplicativeError[EitherE[E], E]:
    override def raiseError[A](e: E): Either[E, A] = Left(e)

    override def handleErrorWith[A](fa: Either[E, A])(f: E => Either[E, A]): Either[E, A] = fa match
      case Left(e)  => f(e)
      case Right(a) => Right(a)

    override def handleError[A](fa: Either[E, A])(f: E => A): Either[E, A] = fa match
      case Left(e)  => Right(f(e))
      case Right(a) => Right(a)

    override def attempt[A](fa: Either[E, A]): Either[E, Either[E, A]] = fa match
      case Left(e)  => Right(Left(e))
      case Right(a) => Right(Right(a))

    override def pure[A](a: A): Either[E, A] = Applicative[EitherE[E]].pure(a)

    override def ap[A, B](fab: Either[E, A => B])(fa: Either[E, A]): Either[E, B] =
      Applicative[EitherE[E]].ap(fab)(fa)

  given [F[_]: Monad, E]: ApplicativeError[EitherTF[F, E], E] = new ApplicativeError[EitherTF[F, E], E]:
    override def raiseError[A](e: E): EitherT[F, E, A] = EitherT(Left(e).pure[F])

    override def handleErrorWith[A](fa: EitherT[F, E, A])(f: E => EitherT[F, E, A]): EitherT[F, E, A] =
      EitherT(fa.value.flatMap(_ match
        case Left(e)  => f(e).value
        case Right(a) => Right(a).pure[F]
      ))

    override def handleError[A](fa: EitherT[F, E, A])(f: E => A): EitherT[F, E, A] =
      EitherT(fa.value.flatMap(_ match
        case Left(e)  => Right(f(e)).pure[F]
        case Right(a) => Right(a).pure[F]
      ))

    override def attempt[A](fa: EitherT[F, E, A]): EitherT[F, E, Either[E, A]] =
      EitherT(fa.value.flatMap(_ match
        case Left(e)  => Right(Left(e)).pure[F]
        case Right(a) => Right(Right(a)).pure[F]
      ))

    override def pure[A](a: A): EitherT[F, E, A] = Applicative[EitherTF[F, E]].pure(a)

    override def ap[A, B](fab: EitherT[F, E, A => B])(fa: EitherT[F, E, A]): EitherT[F, E, B] =
      Applicative[EitherTF[F, E]].ap(fab)(fa)

object ApplicativeErrorSyntax:
  extension [E] (e: E)
    def raiseError[F[_], A](using ApplicativeError[F, E]): F[A] = ApplicativeError[F, E].raiseError(e)
