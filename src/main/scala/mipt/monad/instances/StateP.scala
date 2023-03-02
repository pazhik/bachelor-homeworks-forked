package mipt.monad.instances

import mipt.monad.Monad

type StateP[S, A] = S => (A, S)
type StateS[S] = [A] =>> StateP[S, A]

object StateP:
  given [S]: Monad[StateS[S]] = new Monad[StateS[S]]:
    override def pure[A](a: A): StateP[S, A] = s => (a, s)

    override def flatMap[A, B](fa: StateP[S, A])(f: A => StateP[S, B]): StateP[S, B] = s =>
      val (a, s1) = fa(s)
      f(a)(s1)
