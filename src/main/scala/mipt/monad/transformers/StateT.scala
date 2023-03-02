package mipt.monad.transformers

import mipt.monad.Monad

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

case class StateT[F[_]: Monad, S, A](value: S => F[(A, S)])
type StateTF[F[_], S] = [A] =>> StateT[F, S, A]

object StateT:
  given [F[_]: Monad, S]: Monad[StateTF[F, S]] = new Monad[StateTF[F, S]]:
    override def pure[A](a: A): StateT[F, S, A] = StateT(s => (a, s).pure[F])

    override def flatMap[A, B](fa: StateT[F, S, A])(f: A => StateT[F, S, B]): StateT[F, S, B] =
      StateT(s => for {
        st1 <- fa.value(s)
        (a, s1) = st1
        st2 <- f(a).value(s1)
      } yield st2)
