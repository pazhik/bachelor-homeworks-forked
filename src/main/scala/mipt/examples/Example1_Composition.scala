package mipt.examples

import mipt.auxiliary.MonoidP
import mipt.monad.{Functor, Monad}
import mipt.monad.instances.{ReaderP, ReaderR, WriterP, WriterW}
import mipt.monad.instances.ReaderP.given
import mipt.monad.FunctorSyntax.*

object Question1_Composition:
  type Result
  given MonoidP[Result] = ???

  def readConfig: ReaderP[Int, Int] = ???
  def proceedValue: Int => Result = ???
  def logResult: Result => WriterP[Result, Unit] = ???

  readConfig.map(proceedValue).map(logResult): ReaderP[Int, WriterP[Result, Unit]] // How to compose with Unit => A???

case class ComposedFunctor[F[_]: Functor, G[_]: Functor, A](value: F[G[A]])
type ComposedFunctorFG[F[_], G[_]] = [A] =>> ComposedFunctor[F, G, A]

object ComposedFunctor:
  given [F[_]: Functor, G[_]: Functor]: Functor[ComposedFunctorFG[F, G]] = new Functor[ComposedFunctorFG[F, G]]:
    override def fmap[A, B](fa: ComposedFunctor[F, G, A])(f: A => B): ComposedFunctor[F, G, B] =
      ComposedFunctor(fa.value.map(_.map(f)))

object Example1_Composition:
  type Result = String

  def readConfig: ReaderP[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => WriterP[Result, Unit] = r => WriterP(r, ())

  @main def e1: Unit =
    val composition: ReaderP[Int, WriterP[Result, Unit]] = readConfig.map(proceedValue).map(logResult)

    val wrappedComposition: ComposedFunctor[ReaderR[Int], WriterW[Result], Unit] = ComposedFunctor(composition)
    val mappedComposition: ComposedFunctor[ReaderR[Int], WriterW[Result], Int] =
      wrappedComposition.map(_ =>
        println("Yeah!")
        42
      )

    println(mappedComposition.value(2023))
