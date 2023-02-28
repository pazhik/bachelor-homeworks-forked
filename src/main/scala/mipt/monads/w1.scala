package mipt.monads

import scala.util.Try
import mipt.functors.Functor
import mipt.functors.functorSyntax.map

// Recap: functors

trait Eff[x]

def metricValue: Eff[Double] = ???

val reqSla: Double => Boolean = _ > 0.95

def computeReqSla(using Functor[Eff]): Eff[Boolean] =
  metricValue.map(reqSla)

// and then we want to send alerts depended on result

def makeAlerts: Boolean => Eff[Unit] =
  case true => ???
  case false => ???

//def computeAndSendAlerts(using ???[F]): F[Unit] =
//  computeReqSla ??? makeAlerts

trait FlatMap[F[_]] extends Functor[F]:
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  // def >>=

object syntax:
  extension [A, F[_]] (fa: F[A])
    def flatMap[B](f: A => F[B])(using F: FlatMap[F]): F[B] = F.flatMap(fa)(f)

import syntax.*

// example
enum EitherP[+A, +B]:
  case Left[A](a: A) extends EitherP[A, Nothing]
  case Right[B](b: B) extends EitherP[Nothing, B]

import EitherP.{Left, Right}

object EitherP:
  given [E]: FlatMap[EitherP[E, *]] = new FlatMap[EitherP[E, *]]:
    override def flatMap[A, B](fa: EitherP[E, A])(f: A => EitherP[E, B]): EitherP[E, B] =
      fa match {
        case Left(err) => Left(err)
        case Right(x) => f(x)
      }

    override def map[A, B](fa: EitherP[E, A])(f: A => B): EitherP[E, B] =
      fa match {
        case Left(err) => Left(err)
        case Right(x) => Right(f(x))
      }

def safeDiv(x: Double, y: Double, expr: String = ""): EitherP[String, Double] =
  if (y == 0) Left(s"$expr equals 0")
  else Right(x / y)

// 1 / (1 - 1 / (1 - x))
def eitherComp(x: Double) = for {
  rel <- safeDiv(1, 1 - x, "1 - x")
  y = 1 - rel
  res <- safeDiv(1, y, "1 - 1 / (1 - x)")
} yield res

@main def _1: Unit =
  println(eitherComp(1))
  println(eitherComp(0))
  println(eitherComp(0.5))

// What we need to express map via flatMap
