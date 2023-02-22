package mipt.functors

import functorSyntax.*

// variance and functors
/**
 * A <: B
 *
 *
 *
 * F[+x]
 * forall A, B: A <: B => F[A] <: F[B]
 *
 * F[-x]
 * forall A, B: A <: B => F[A] >: F[B]
 */

def widen[F[_]: Functor, A <: B, B](fa: F[A]): F[B] = fa.asInstanceOf[F[B]]

def widenC[F[_]: Contravariant, A >: B, B](fa: F[A]): F[B] = ???



// Существует ли F[_] с lawful инстансом функтора/контраварианта, который естественным образом нельзя сделaть ковариантным?

// Существует ли ковариантный F[+_], который не является функтором?

//trait F[+A] {
//  def foo: F[A]
//  def goo(a: F[A] => ):
//}

type Foo2[-C, +A] = C => (List[A] => C) => A

given Functor[Foo2[Int, *]] = new Functor[Foo2[Int, *]] {
  override def map[A, B](fa: Int => (List[A] => Int) => A)(f: A => B): Int => (List[B] => Int) => B =
    i => g =>

}

sealed trait Foo[+A]
case object N extends Foo[Nothing]
case class Acc[A](a: Foo[A]) extends Foo[Nothing]

@main def _5test1() = {
  println("Password: " + UserPassword("admin").show)
}
