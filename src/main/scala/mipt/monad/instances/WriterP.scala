package mipt.monad.instances

import mipt.auxiliary.MonoidP
import mipt.monad.Monad

case class WriterP[W: MonoidP, A](log: W, value: A)
type WriterW[W] = [A] =>> WriterP[W, A]

object WriterP:
  given [W: MonoidP]: Monad[WriterW[W]] = new Monad[WriterW[W]]:
    override def pure[A](a: A): WriterP[W, A] = WriterP(MonoidP[W].empty, a)

    override def flatMap[A, B](fa: WriterP[W, A])(f: A => WriterP[W, B]): WriterP[W, B] =
      val WriterP(l1, a) = fa
      val WriterP(l2, b) = f(a)
      WriterP(MonoidP[W].combine(l1, l2), b)
