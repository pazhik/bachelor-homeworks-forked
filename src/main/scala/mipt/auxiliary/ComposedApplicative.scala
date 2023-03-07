package mipt.auxiliary

import cats.Applicative
import mipt.examples.{Composed, ComposedFG}

import mipt.monad.ApplicativeSyntax.*

object ComposedApplicative:
  given [F[_]: Applicative, G[_]: Applicative]: Applicative[ComposedFG[F, G]] =
    new Applicative[ComposedFG[F, G]]:
      override def pure[A](a: A): Composed[F, G, A] = Composed(a.pure[G].pure[F])

      //                    F[G[A => B]]                 F[G[A]]
      override def ap[A, B](fab: Composed[F, G, A => B])(fa: Composed[F, G, A]): Composed[F, G, B] =
        Composed(Applicative[G].ap[A, B].pure[F].ap(fab.value).ap(fa.value))
        
// Applicative[G].ap: G[A => B] => (G[A] => G[B])
// ap[G].pure[F]: F[G[A => B] => (G[A] => G[B])]
// ap[G].pure[F].ap(fab): F[G[A] => G[B]]
// ap[G].pure[F].ap(fab).ap(fa): F[G[B]]
