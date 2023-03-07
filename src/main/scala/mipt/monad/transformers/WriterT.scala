package mipt.monad.transformers

import cats.{Monad, Monoid}
import mipt.monad.instances.Writer
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

import scala.annotation.tailrec

case class WriterT[F[_], W: Monoid, A](value: F[Writer[W, A]])
type WriterTF[F[_], W] = [A] =>> WriterT[F, W, A]

object WriterT:
  given [F[_]: Monad, W: Monoid]: Monad[WriterTF[F, W]] = new Monad[WriterTF[F, W]]:
    override def pure[A](a: A): WriterT[F, W, A] = WriterT(Writer(Monoid[W].empty, a).pure[F])

    override def flatMap[A, B](fa: WriterT[F, W, A])(f: A => WriterT[F, W, B]): WriterT[F, W, B] =
      WriterT(for {
        w1           <- fa.value
        Writer(l1, a) = w1
        w2           <- f(a).value
        Writer(l2, b) = w2
      } yield Writer(Monoid[W].combine(l1, l2), b))

    override def tailRecM[A, B](a: A)(f: A => WriterT[F, W, Either[A, B]]): WriterT[F, W, B] =
      WriterT(Monad[F].tailRecM(Writer(Monoid[W].empty, a))(w =>
        val Writer(l1, a) = w
        f(a).value.map(_ match
          case Writer(l2, Left(a))  => Left(Writer(Monoid[W].combine(l1, l2), a))
          case Writer(l2, Right(b)) => Right(Writer(Monoid[W].combine(l1, l2), b))
        )
      ))
