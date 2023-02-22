package mipt.functors

import cats.Contravariant
import functorSyntax.*

// All F[_] are functors?
trait Show[A] {
  def show(t: A): String
}

object Show {
  given Show[String] = (t: String) => t
}

case class UserPassword(pass: String)
object UserPassword {
  given Show[UserPassword] = (t: UserPassword) => "*" * t.pass.length
}

extension [A] (x: A)
  def show(using s: Show[A]): String = s.show(x)

@main def _4test1() = {
  println("Password: " + UserPassword("admin").show)
}

// Try to make functor instance
given Functor[Show] = new Functor[Show] {
//  // A => String
//  // B => A
//  // B => String
  override def map[A, B](fa: Show[A])(f: A => B): Show[B] = ???

//  override def contramap[A, B](fa: Show[A])(f: B => A): Show[B] = ???
}

// ohh, let's invent
trait Contravariant[F[_]] {
  def contramap[A, B](fa: F[A])(f: B => A): F[B]
  def liftC[A, B](f: B => A): F[A] => F[B] = contramap(_)(f)
}

extension [A, F[_]] (fa: F[A])
  def contramap[B](f: B => A)(using F: Contravariant[F]): F[B] = F.contramap(fa)(f)

given Contravariant[Show] = new Contravariant[Show]:
  override def contramap[A, B](fa: Show[A])(f: B => A): Show[B] = (t: B) => fa.show(f(t))

// board

// more Examples: Function, Encoder

//type F[A, B] = A => B

given [X]: Functor[X => *] = new Functor[X => *] {
  override def map[A, B](fa: X => A)(f: A => B): X => B = f compose fa
}

given [X]: Contravariant[* => X] = new Contravariant[* => X] {
  override def contramap[A, B](fa: A => X)(f: B => A): B => X = fa compose f
}

type G[A] = A => A