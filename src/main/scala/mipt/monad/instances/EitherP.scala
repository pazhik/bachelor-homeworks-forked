package mipt.monad.instances

import mipt.monad.Monad

enum EitherP[+E, +A]:
  case LeftP[E](e: E) extends EitherP[E, Nothing]
  case RightP[A](a: A) extends EitherP[Nothing, A]
type EitherE[E] = [A] =>> EitherP[E, A]

object EitherP:
  given [E]: Monad[EitherE[E]] = new Monad[EitherE[E]]:
    override def pure[A](a: A): EitherP[E, A] = RightP(a)

    override def flatMap[A, B](fa: EitherP[E, A])(f: A => EitherP[E, B]): EitherP[E, B] = fa match
      case LeftP(e)  => LeftP(e)
      case RightP(a) => f(a)
