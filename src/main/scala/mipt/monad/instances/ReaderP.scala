package mipt.monad.instances

import mipt.monad.Monad

type ReaderP[R, A] = R => A
type ReaderR[R] = [A] =>> ReaderP[R, A]

object ReaderP:
  given [R]: Monad[ReaderR[R]] = new Monad[ReaderR[R]]:
    override def pure[A](a: A): ReaderP[R, A] = _ => a
    override def flatMap[A, B](fa: ReaderP[R, A])(f: A => ReaderP[R, B]): ReaderP[R, B] = r => f(fa(r))(r)
