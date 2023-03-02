package mipt.monad

trait Functor[F[_]]:
  def fmap[A, B](fa: F[A])(f: A => B): F[B]

object Functor:
  def apply[F[_]](using Functor[F]): Functor[F] = summon[Functor[F]]

object FunctorSyntax:
  extension[F[_]: Functor, A] (fa: F[A])
    def map[B](f: A => B): F[B] = summon[Functor[F]].fmap(fa)(f)
