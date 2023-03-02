package mipt.monad.instances

import mipt.monad.Monad

type IdP[A] = A

object IdP:
  given Monad[IdP] = new Monad[IdP]:
    override def pure[A](a: A): IdP[A] = a
    override def flatMap[A, B](fa: IdP[A])(f: A => IdP[B]): IdP[B] = f(fa)
