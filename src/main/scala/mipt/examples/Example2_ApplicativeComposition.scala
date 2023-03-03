package mipt.examples

import mipt.auxiliary.MonoidP
import mipt.monad.Applicative
import mipt.monad.instances.{ReaderP, ReaderR, WriterP, WriterW}
import mipt.monad.instances.ReaderP.given
import mipt.monad.instances.WriterP.given

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*

object Question2_ApplicativeComposition:
  type Result
  given MonoidP[Result] = ???

  def readConfig: ReaderP[Int, Int] = ???
  def proceedValue: Int => Result = ???
  def logResult: Result => WriterP[Result, Unit] = ???

  def anotherComputation: ReaderP[Int, WriterP[Result, Unit => Int]] = ??? // How to compose with our result???

case class ComposedApplicative[F[_]: Applicative, G[_]: Applicative, A](value: F[G[A]])
type ComposedApplicativeFG[F[_], G[_]] = [A] =>> ComposedApplicative[F, G, A]

object ComposedApplicative:
  given [F[_]: Applicative, G[_]: Applicative]: Applicative[ComposedApplicativeFG[F, G]] =
    new Applicative[ComposedApplicativeFG[F, G]]:
      override def pure[A](a: A): ComposedApplicative[F, G, A] = ComposedApplicative(a.pure[G].pure[F])

      override def ap[A, B](fab: ComposedApplicative[F, G, A => B])(fa: ComposedApplicative[F, G, A]): ComposedApplicative[F, G, B] =
        ComposedApplicative(Applicative[G].ap[A, B].pure[F].ap(fab.value).ap(fa.value))

object Example2_ApplicativeComposition:
  type Result = String

  def readConfig: ReaderP[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => WriterP[Result, Unit] = r => WriterP(r, ())

  def anotherComputation: ReaderP[Int, WriterP[Result, Unit => Int]] =
    ((_: Unit) =>
      println("Yeah!")
      42
    ).pure[WriterW[Result]].pure[ReaderR[Int]]

  @main def e2: Unit =
    val composition: ReaderP[Int, WriterP[Result, Unit]] = readConfig.map(proceedValue).map(logResult)

    val wrappedComposition: ComposedApplicative[ReaderR[Int], WriterW[Result], Unit] = ComposedApplicative(composition)
    val appedComposition: ComposedApplicative[ReaderR[Int], WriterW[Result], Int] =
      ComposedApplicative(anotherComputation).ap(wrappedComposition)

    println(appedComposition.value(2023))
