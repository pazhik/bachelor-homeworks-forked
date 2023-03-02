package mipt.monad.instances

import mipt.monad.Monad

enum OptionP[+A]:
  case SomeP(a: A) extends OptionP[A]
  case NoneP       extends OptionP[Nothing]

object OptionP:
  given Monad[OptionP] = new Monad[OptionP]:
    override def pure[A](a: A): OptionP[A] = SomeP(a)

    override def flatMap[A, B](fa: OptionP[A])(f: A => OptionP[B]): OptionP[B] = fa match
      case SomeP(a) => f(a)
      case NoneP    => NoneP
