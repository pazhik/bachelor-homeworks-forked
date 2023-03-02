package mipt.monad.transformers

import mipt.monad.Monad
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.MonadSyntax.*
import mipt.monad.instances.OptionP
import mipt.monad.instances.OptionP.*

case class OptionT[F[_]: Monad, A](value: F[OptionP[A]])
type OptionTF[F[_]] = [A] =>> OptionT[F, A]

object OptionT:
  given [F[_]: Monad]: Monad[OptionTF[F]] = new Monad[OptionTF[F]]:
    override def pure[A](a: A): OptionT[F, A] = OptionT(SomeP(a).pure[F])

    override def flatMap[A, B](fa: OptionT[F, A])(f: A => OptionT[F, B]): OptionT[F, B] =
      OptionT(fa.value.flatMap(_ match
        case SomeP(a) => f(a).value
        case NoneP    => NoneP.pure[F]
      ))
