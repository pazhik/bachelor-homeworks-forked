package mipt.monads

import scala.util.Try
import mipt.functors.Functor
import mipt.functors.functorSyntax.map

type Id[x] = x

// Pointed (alley cats - Pure)

trait Pointed[F[_]] extends Functor[F]:
  def pure[A](a: A): F[A] //= map(unit, _ => a)
//  def unit: F[Unit]

object Pointed:
  object syntax:
    extension [A] (a: A)
      def pure[F[_]](using P: Pointed[F]): F[A] = P.pure(a)


// another encoding of dependencies
trait PointedP[F[_]]:
  def functor: Functor[F]
  def pure[A](a: A): F[A]

trait FlatMapP[F[_]]:
  def functor: Functor[F]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

// no guarantees that functor instance in PointedP and FlatMapP is the same!
//def foo[F[_]: PointedP: FlatMapP]: Unit = Functor[F].map(...)


// Monad
trait Monad[F[_]] extends Pointed[F], FlatMap[F]:
  override def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(x => pure(f(x)))

  type Compose[F[_], G[_], A] = F[G[A]]
//  def flatten[A]: F[F[A]] => F[A] = flatMap(_)(identity)
//  def flatten: Compose[F, F, *] ~> F[*]

//    def pure2[A]:   Id[A] => F[A] = pure
//  def pure2: Id ~> F[*] = ???

object Monad:
  @inline def apply[F[_]](using M: Monad[F]): Monad[F] = M

  given Monad[List] = new Monad[List]:
    override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] = fa.flatMap(f)
    override def pure[A](a: A): List[A] = List(a)


// natural transformation
def identity[A]: A => A = x => x


val identityP: [A] => A => A =
  [A] => (x: A) => x

type ~>[F[_], G[_]] = [A] => F[A] => G[A]

//trait ~>[F[_], G[_]]:
//  def apply[A]: F[A] => G[A]

val head: List ~> Option = [A] => (list: List[A]) =>
  list match
    case Nil => None
    case h :: _ => Some(h)

@main def _2: Unit = ()

// go to board
