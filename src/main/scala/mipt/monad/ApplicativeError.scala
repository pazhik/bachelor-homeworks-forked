package mipt.monad

import mipt.monad.instances.{EitherE, EitherP}
import mipt.monad.instances.EitherP.*
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
  given [E]: ApplicativeError[EitherE[E], E] = new ApplicativeError[EitherE[E], E]:
    override def raiseError[A](e: E): EitherP[E, A] = LeftP(e)

    override def handleErrorWith[A](fa: EitherP[E, A])(f: E => EitherP[E, A]): EitherP[E, A] = fa match
      case LeftP(e)  => f(e)
      case RightP(a) => RightP(a)

    override def handleError[A](fa: EitherP[E, A])(f: E => A): EitherP[E, A] = fa match
      case LeftP(e)  => RightP(f(e))
      case RightP(a) => RightP(a)

    override def attempt[A](fa: EitherP[E, A]): EitherP[E, Either[E, A]] = fa match
      case LeftP(e)  => RightP(Left(e))
      case RightP(a) => RightP(Right(a))

    override def pure[A](a: A): EitherP[E, A] = Applicative[EitherE[E]].pure(a)

    override def ap[A, B](fab: EitherP[E, A => B])(fa: EitherP[E, A]): EitherP[E, B] =
      Applicative[EitherE[E]].ap(fab)(fa)

  given [F[_]: Monad, E]: ApplicativeError[EitherTF[F, E], E] = new ApplicativeError[EitherTF[F, E], E]:
    override def raiseError[A](e: E): EitherT[F, E, A] = EitherT(LeftP(e).pure[F])

    override def handleErrorWith[A](fa: EitherT[F, E, A])(f: E => EitherT[F, E, A]): EitherT[F, E, A] =
      EitherT(fa.value.flatMap(_ match
        case LeftP(e)  => f(e).value
        case RightP(a) => RightP(a).pure[F]
      ))

    override def handleError[A](fa: EitherT[F, E, A])(f: E => A): EitherT[F, E, A] =
      EitherT(fa.value.flatMap(_ match
        case LeftP(e)  => RightP(f(e)).pure[F]
        case RightP(a) => RightP(a).pure[F]
      ))

    override def attempt[A](fa: EitherT[F, E, A]): EitherT[F, E, Either[E, A]] =
      EitherT(fa.value.flatMap(_ match
        case LeftP(e)  => RightP(Left(e)).pure[F]
        case RightP(a) => RightP(Right(a)).pure[F]
      ))

    override def pure[A](a: A): EitherT[F, E, A] = Applicative[EitherTF[F, E]].pure(a)

    override def ap[A, B](fab: EitherT[F, E, A => B])(fa: EitherT[F, E, A]): EitherT[F, E, B] =
      Applicative[EitherTF[F, E]].ap(fab)(fa)
