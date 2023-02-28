package mipt.monads

import mipt.functors.Functor
import mipt.functors.functorSyntax.map

// monads composition problem
// is there smth weaker but still composable?
// functors again

val metricValue2: Eff[Double] = ???

// another requirement: compare with previous value

def prevMetricValue: Eff[Double] = ???

//def computeReq2(using BlaBla[F[_]]): F[Boolean] = ???
//  (metricValue product prevMetricValue).map(_ > _)


// applicatives here are
trait Apply[F[_]] extends Functor[F]:
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    ap(map(fa)(a => (b: B) => (a, b)))(fb)


@main def _6(): Unit = {}

