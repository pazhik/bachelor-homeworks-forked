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
  def logResult: Result => Writer[Result, Unit] = ???

  def anotherComputation: Reader[Int, Writer[Result, Unit => Int]] = ??? // How to compose with our result???

object Example2_ApplicativeComposition:
  type Result = String

  def readConfig: Reader[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => Writer[Result, Unit] = r => Writer(r, ())

  def anotherComputation: Reader[Int, Writer[Result, Unit => Int]] =
    ((_: Unit) =>
      println("Yeah!")
      42
    ).pure[WriterW[Result]].pure[ReaderR[Int]]

  @main def e2: Unit =
    val composition: Reader[Int, Writer[Result, Unit]] = readConfig.map(proceedValue).map(logResult)

    val wrappedComposition: Composed[ReaderR[Int], WriterW[Result], Unit] = Composed(composition)
    val appedComposition: Composed[ReaderR[Int], WriterW[Result], Int] =
      Composed(anotherComputation).ap(wrappedComposition)

    println(appedComposition.value(2023))
