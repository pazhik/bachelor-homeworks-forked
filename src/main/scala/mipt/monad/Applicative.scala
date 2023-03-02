package mipt.monad

trait Applicative[F[_]] extends Functor[F]:
  def pure[A](a: A): F[A]
  def ap[A, B](fab: F[A => B])(fa: F[A]): F[B]

  override def fmap[A, B](fa: F[A])(f: A => B): F[B] = ap(pure(f))(fa)

object Applicative:
  def apply[F[_]](using Applicative[F]): Applicative[F] = summon[Applicative[F]]

object ApplicativeSyntax:
  extension [A] (a: A)
    def pure[F[_]: Applicative] = summon[Applicative[F]].pure(a)
    
  extension[F[_]: Applicative, A, B] (fab: F[A => B])
    def ap(fa: F[A]): F[B] = summon[Applicative[F]].ap(fab)(fa)
