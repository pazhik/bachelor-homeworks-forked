package mipt.monad.instances

import cats.Monad

import scala.annotation.tailrec

type State[S, A] = S => (A, S)
type StateS[S] = [A] =>> State[S, A]

object State:
  given [S]: Monad[StateS[S]] = new Monad[StateS[S]]:
    override def pure[A](a: A): State[S, A] = s => (a, s)

    override def flatMap[A, B](fa: State[S, A])(f: A => State[S, B]): State[S, B] = s =>
      val (a, s1) = fa(s)
      f(a)(s1)

    override def tailRecM[A, B](a: A)(f: A => State[S, Either[A, B]]): State[S, B] = s => f(a)(s) match
      case (Left(a), s)  => tailRecM(a)(f)(s)
      case (Right(b), s) => (b, s)
