package mipt.functors

import functorSyntax.*

// variance and functors
/**
 * A <: B
 *
 *
 *
 * F[+x]
 * forall A, B: A <: B => F[A] <: F[B]
 *
 * F[-x]
 * forall A, B: A <: B => F[A] >: F[B]
 */

def widen[F[_]: Functor, A <: B, B](fa: F[A]): F[B] = fa.asInstanceOf[F[B]]

def widenC[F[_]: Contravariant, A >: B, B](fa: F[A]): F[B] = ???



// Существует ли F[_] с lawful инстансом функтора/контраварианта, который естественным образом нельзя сделaть ковариантным?

// Существует ли ковариантный F[+_], который не является функтором?

@main def _5test1() = {}
