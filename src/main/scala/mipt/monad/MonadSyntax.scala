package mipt.monad

import cats.Monad

object MonadSyntax:
  extension[F[_], A] (fa: F[A])
    def flatMap[B](f: A => F[B])(using Monad[F]): F[B] = summon[Monad[F]].flatMap(fa)(f)

  extension[F[_], A] (ffa: F[F[A]])
    def flatten(using Monad[F]): F[A] = summon[Monad[F]].flatten(ffa)
