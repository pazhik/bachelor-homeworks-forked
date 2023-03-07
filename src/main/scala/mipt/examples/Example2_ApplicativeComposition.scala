package mipt.examples

import cats.Applicative
import mipt.auxiliary.ComposedApplicative.given
import mipt.monad.instances.{Reader, ReaderR, Writer, WriterW}
import mipt.monad.instances.Reader.given
import mipt.monad.instances.Writer.given

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*

object Question2_ApplicativeComposition:
  type Result = String

  def readConfig: Reader[Int, Int] = ???
  def proceedValue: Int => Result = ???
  def logResult: Result => Writer[Result, Result] = ???

  def anotherComputation: Reader[Int, Writer[Result, Result => Int]] = ??? // How to compose with our result???

object Example2_ApplicativeComposition:
  type Result = String

  def readConfig: Reader[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => Writer[Result, Result] = r => Writer(r, r)

  def anotherComputation: Reader[Int, Writer[Result, Result => Int]] =
    ((_: Result).length).pure[WriterW[Result]].pure[ReaderR[Int]]

  @main def e2: Unit =
    val composition: Reader[Int, Writer[Result, Result]] = readConfig.map(proceedValue).map(logResult)

    val wrappedComposition: Composed[ReaderR[Int], WriterW[Result], Result] = Composed(composition)
    val appedComposition: Composed[ReaderR[Int], WriterW[Result], Int] =
      Composed(anotherComputation).ap(wrappedComposition)

    println(appedComposition.value(2023))
