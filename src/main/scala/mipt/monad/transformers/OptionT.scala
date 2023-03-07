package mipt.monad.transformers

import cats.Monad
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

import scala.annotation.tailrec

case class OptionT[F[_], A](value: F[Option[A]])
type OptionTF[F[_]] = [A] =>> OptionT[F, A]

object OptionT:
  given [F[_]: Monad]: Monad[OptionTF[F]] = new Monad[OptionTF[F]]:
    override def pure[A](a: A): OptionT[F, A] = OptionT(Some(a).pure[F])

    override def flatMap[A, B](fa: OptionT[F, A])(f: A => OptionT[F, B]): OptionT[F, B] =
      OptionT(fa.value.flatMap(_ match
        case Some(a) => f(a).value
        case None    => None.pure[F]
      ))

    override def tailRecM[A, B](a: A)(f: A => OptionT[F, Either[A, B]]): OptionT[F, B] =
      OptionT(
        Monad[F].tailRecM(a)(a => f(a).value.map(_ match
          case Some(Left(a))  => Left(a)
          case Some(Right(b)) => Right(Some(b))
          case None           => Right(None)
        ))
      )
