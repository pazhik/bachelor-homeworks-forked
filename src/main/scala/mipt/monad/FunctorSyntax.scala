package mipt.monad

import cats.Functor

object FunctorSyntax:
  extension [A, B] (f: A => B)
    def lift[F[_]: Functor]: F[A] => F[B] = Functor[F].lift(f)
  
  extension [F[_], A] (fa: F[A])
    def map[B](f: A => B)(using Functor[F]): F[B] = Functor[F].fmap(fa)(f)
