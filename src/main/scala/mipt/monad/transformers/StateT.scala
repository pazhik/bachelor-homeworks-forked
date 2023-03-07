package mipt.monad.transformers

import cats.{Functor, Monad}
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

import scala.annotation.tailrec

case class StateT[F[_], S, A](value: S => F[(A, S)])
type StateTF[F[_], S] = [A] =>> StateT[F, S, A]

object StateT:
  def fromFA[F[_]: Functor, S, A](fa: F[A]): StateT[F, S, A] = StateT(s => fa.map(a => (a, s)))

  given [F[_]: Monad, S]: Monad[StateTF[F, S]] = new Monad[StateTF[F, S]]:
    override def pure[A](a: A): StateT[F, S, A] = StateT(s => (a, s).pure[F])

    override def flatMap[A, B](fa: StateT[F, S, A])(f: A => StateT[F, S, B]): StateT[F, S, B] =
      StateT(s => for {
        st1 <- fa.value(s)
        (a, s1) = st1
        st2 <- f(a).value(s1)
      } yield st2)

    override def tailRecM[A, B](a: A)(f: A => StateT[F, S, Either[A, B]]): StateT[F, S, B] =
      StateT(s => Monad[F].tailRecM((a, s)){ case (a, s) => f(a).value(s).map(_ match
        case (Left(a), s)  => Left(a, s)
        case (Right(b), s) => Right(b, s)
      )})
