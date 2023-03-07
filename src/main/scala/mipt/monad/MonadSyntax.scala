package mipt.monad

import cats.Monad

object MonadSyntax:
  extension[F[_]: Monad, A] (fa: F[A])
    def flatMap[B](f: A => F[B]): F[B] = summon[Monad[F]].flatMap(fa)(f)

  extension[F[_]: Monad, A] (ffa: F[F[A]])
    def flatten: F[A] = summon[Monad[F]].flatten(ffa)
