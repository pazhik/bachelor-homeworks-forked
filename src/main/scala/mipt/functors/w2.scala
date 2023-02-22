package mipt.functors

import scala.util.{Failure, Try}

// How to make it abstract?
def lengthList(x: List[String]): List[Int] = x.map(_.length)
def lengthOption(x: Option[String]): Option[Int] = x.map(_.length)
def lengthTry(x: Try[String]): Try[Int] = x.map(_.length)

@main def _2test1(): Unit = {
  println(lengthList(List("foo", "s", "")))
  println(lengthOption(Some("exise")))
  println(lengthTry(Failure(new Exception("no pls"))))
}

//def lengthAny[F[_]](x: F[String])(using F: Blabla): F[Int] = F.map(x, _.length)

//def lengthAbstr[F[_], A, B](fa: F[A])(f: A => B)(using Functor[F]): F[B] = fa.map(f)

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
  def lift[A, B](f: A => B): F[A] => F[B] = map(_)(f)
}

trait FunctorInstances {
  given Functor[List] = new Functor[List] {
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
  }

  given Functor[Option] = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }

  given Functor[Try] = new Functor[Try] {
    override def map[A, B](fa: Try[A])(f: A => B): Try[B] = fa.map(f)
  }
}

def lengthAny[F[_]](x: F[String])(using F: Functor[F]): F[Int] = F.map(x)(_.length)

@main def _2test2(): Unit = {
  println(lengthAny(List("foo", "s", "")))
  println(lengthAny(Option("exist")))
  println(lengthAny[Try](Failure(new Exception("no pls"))))
}

object Functor extends FunctorInstances {
  @inline def apply[F[_]](using F: Functor[F]): Functor[F] = F
}

object functorSyntax {
  extension [F[_], A](fa: F[A]) def map[B](f: A => B)(using F: Functor[F]): F[B] = F.map(fa)(f)
}

import functorSyntax.*

def lengthAnyP[F[_]: Functor](x: F[String]): F[Int] = x.map(_.length)

// Effect
def continue[Effect[_], Prev, Next](now: Effect[Prev], next: Prev => Next): Effect[Next] = ???

// Open board and draw pictures


