package mipt.monad.instances

import cats.Monad

import scala.annotation.tailrec

type Reader[R, A] = R => A
type ReaderR[R] = [A] =>> Reader[R, A]

object Reader:
  given [R]: Monad[ReaderR[R]] = new Monad[ReaderR[R]]:
    override def pure[A](a: A): Reader[R, A] = _ => a
    override def flatMap[A, B](fa: Reader[R, A])(f: A => Reader[R, B]): Reader[R, B] = r => f(fa(r))(r)
  
    override def tailRecM[A, B](a: A)(f: A => Reader[R, Either[A, B]]): Reader[R, B] = r => f(a)(r) match
      case Left(a)  => tailRecM(a)(f)(r)
      case Right(b) => b
