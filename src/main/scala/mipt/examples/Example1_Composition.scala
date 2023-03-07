package mipt.examples

import cats.Functor
import mipt.auxiliary.ComposedFunctor.given
import mipt.monad.instances.{Reader, ReaderR, Writer, WriterW}
import mipt.monad.instances.Reader.given
import mipt.monad.FunctorSyntax.*

object Question1_Composition:
  type Result = String

  def readConfig: Reader[Int, Int] = ???
  def proceedValue: Int => Result = ???
  def logResult: Result => Writer[Result, Result] = ???

  readConfig.map(proceedValue).map(logResult): Reader[Int, Writer[Result, Result]] // How to compose with Result => A???

case class Composed[F[_], G[_], A](value: F[G[A]])
type ComposedFG[F[_], G[_]] = [A] =>> Composed[F, G, A]

object Example1_Composition:
  type Result = String

  def readConfig: Reader[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => Writer[Result, Result] = r => Writer(r, r)

  def afterWork: Result => Int = _.length

  @main def e1: Unit =
    val composition: Reader[Int, Writer[Result, Result]] = readConfig.map(proceedValue).map(logResult)

    val wrappedComposition: Composed[ReaderR[Int], WriterW[Result], Result] = Composed(composition)
    val mappedComposition: Composed[ReaderR[Int], WriterW[Result], Int] =
      wrappedComposition.map(afterWork)

    println(mappedComposition.value(2023))
