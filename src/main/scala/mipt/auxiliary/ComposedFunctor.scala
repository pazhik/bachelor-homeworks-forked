package mipt.auxiliary

import cats.Functor
import mipt.examples.{Composed, ComposedFG}

import mipt.monad.FunctorSyntax.*

object ComposedFunctor:
  given [F[_]: Functor, G[_]: Functor]: Functor[ComposedFG[F, G]] = new Functor[ComposedFG[F, G]]:
    override def map[A, B](fa: Composed[F, G, A])(f: A => B): Composed[F, G, B] =
      Composed(fa.value.map(_.map(f)))
