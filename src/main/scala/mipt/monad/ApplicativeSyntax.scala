package mipt.monad

import cats.Applicative

object ApplicativeSyntax:
  extension [A] (a: A)
    def pure[F[_]: Applicative] = summon[Applicative[F]].pure(a)
    
  extension [F[_], A, B] (fab: F[A => B])
    def ap(fa: F[A])(using Applicative[F]): F[B] = summon[Applicative[F]].ap(fab)(fa)
