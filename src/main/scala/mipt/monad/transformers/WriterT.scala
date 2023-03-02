package mipt.monad.transformers

import mipt.auxiliary.MonoidP
import mipt.monad.Monad
import mipt.monad.instances.WriterP

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

case class WriterT[F[_]: Monad, W: MonoidP, A](value: F[WriterP[W, A]])
type WriterTF[F[_], W] = [A] =>> WriterT[F, W, A]

object WriterT:
  given [F[_]: Monad, W: MonoidP]: Monad[WriterTF[F, W]] = new Monad[WriterTF[F, W]]:
    override def pure[A](a: A): WriterT[F, W, A] = WriterT(WriterP(MonoidP[W].empty, a).pure[F])

    override def flatMap[A, B](fa: WriterT[F, W, A])(f: A => WriterT[F, W, B]): WriterT[F, W, B] =
      WriterT(for {
        w1           <- fa.value
        WriterP(l1, a) = w1
        w2           <- f(a).value
        WriterP(l2, b) = w2
      } yield WriterP(MonoidP[W].combine(l1, l2), b))
