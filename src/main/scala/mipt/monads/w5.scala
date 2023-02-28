package mipt.monads

import mipt.functors.monoidSyntax.|+|
import mipt.functors.Monoid
import mipt.functors.functorSyntax.map
import mipt.monads.Pointed.syntax.pure
import mipt.monads.State.{T, get, put}
import mipt.monads.syntax.*
import java.util.UUID
import scala.annotation.tailrec
import scala.util.Try
import State.run

// let's add tail recursive flatMap

trait MonadRec[F[_]] /* extends Monad[F]*/:
  // part of FlatMap in cats
  def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B]

given [S]: MonadRec[State[S, *]] = new MonadRec[State[S, *]] {
  @tailrec
  def tailRecMAcc[A, B](a: A)(f: A => State[S, Either[A, B]])(init: S): (B, S) =
    val (r, s) = f(a).run(init)
    r match {
      case Left(a) => tailRecMAcc[A, B](a)(f)(s)
      case Right(b) => (b, s)
    }

  override def tailRecM[A, B](a: A)(f: A => State[S, Either[A, B]]): State[S, B] =
    State(s => tailRecMAcc(a)(f)(s))
}

@main def _5(): Unit = {}
