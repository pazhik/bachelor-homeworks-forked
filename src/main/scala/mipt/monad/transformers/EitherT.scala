package mipt.monad.transformers

import mipt.monad.Monad
import mipt.monad.instances.EitherP
import mipt.monad.instances.EitherP.*

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.MonadSyntax.*

case class EitherT[F[_]: Monad, E, A](value: F[EitherP[E, A]])
type EitherTF[F[_], E] = [A] =>> EitherT[F, E, A]

object EitherT:
  given [F[_]: Monad, E]: Monad[EitherTF[F, E]] = new Monad[EitherTF[F, E]]:
    override def pure[A](a: A): EitherT[F, E, A] = EitherT(RightP(a).pure[F])

    override def flatMap[A, B](fa: EitherT[F, E, A])(f: A => EitherT[F, E, B]): EitherT[F, E, B] =
      EitherT(fa.value.flatMap(_ match
        case LeftP(e)  => LeftP(e).pure[F]
        case RightP(a) => f(a).value
      ))
