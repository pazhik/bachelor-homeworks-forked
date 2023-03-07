package mipt.monad.instances

import cats.{Monad, Monoid}

import scala.annotation.tailrec

import mipt.monad.MonadSyntax.*

case class Writer[W: Monoid, A](log: W, value: A)
type WriterW[W] = [A] =>> Writer[W, A]

object Writer:
  given [W: Monoid]: Monad[WriterW[W]] = new Monad[WriterW[W]]:
    override def pure[A](a: A): Writer[W, A] = Writer(Monoid[W].empty, a)

    override def flatMap[A, B](fa: Writer[W, A])(f: A => Writer[W, B]): Writer[W, B] =
      val Writer(l1, a) = fa
      val Writer(l2, b) = f(a)
      Writer(Monoid[W].combine(l1, l2), b)

    override def tailRecM[A, B](a: A)(f: A => Writer[W, Either[A, B]]): Writer[W, B] = f(a) match
      case wa@Writer(_, Left(a)) => wa.flatMap(_ => tailRecM(a)(f))
      case Writer(log, Right(b)) => Writer(log, b)
