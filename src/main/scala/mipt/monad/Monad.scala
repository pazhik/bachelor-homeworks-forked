package mipt.monad

trait Monad[F[_]] extends Applicative[F]:
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  def flatten[A](ffa: F[F[A]]): F[A] = flatMap(ffa)(identity)
  override def ap[A, B](fab: F[A => B])(fa: F[A]): F[B] = flatMap(fa)(a => fmap(fab)(f => f(a)))

object Monad:
  def apply[F[_]](using Monad[F]): Monad[F] = summon[Monad[F]]

object MonadSyntax:
  extension[F[_]: Monad, A] (fa: F[A])
    def flatMap[B](f: A => F[B]): F[B] = summon[Monad[F]].flatMap(fa)(f)

  extension[F[_]: Monad, A] (ffa: F[F[A]])
    def flatten: F[A] = summon[Monad[F]].flatten(ffa)
