package mipt.examples

import cats.Monad
import mipt.monad.instances.{EitherE, Reader, ReaderR, Writer, WriterW}
import mipt.monad.instances.Reader.given
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*
import mipt.monad.transformers.ReaderT

object Example4_MonadTransformers:
  type Result = String
  type PositiveInt = Int

  def readConfig: Reader[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => Writer[Result, Unit] = r => Writer(r, ())

  def writerComputation: Unit => Reader[Int, Writer[Result, Int]] =
    (_: Unit) => {
      println("Yeah!")
      42
    }.pure[WriterW[Result]].pure[ReaderR[Int]]

  def validateValueO: Int => Option[PositiveInt] = i => if i > 0 then Some(i) else None

  def optionComputation: PositiveInt => Reader[Int, Option[Int]] =
    (_: PositiveInt) => {
      println("Yeah!")
      42
    }.pure[Option].pure[ReaderR[Int]]

  def validateValueE: Int => Either[String, PositiveInt] = i => if i > 0 then Right(i) else Left("Positive value expected")

  def eitherComputation: PositiveInt => Reader[Int, Either[String, Int]] =
    (_: PositiveInt) => {
      println("Yeah!")
      42
    }.pure[EitherE[String]].pure[ReaderR[Int]]

  @main def e4: Unit =
    val compositionW: Reader[Int, Writer[Result, Unit]] = readConfig.map(proceedValue).map(logResult)
    val compositionO: Reader[Int, Option[PositiveInt]] = readConfig.map(validateValueO)
    val compositionE: Reader[Int, Either[String, PositiveInt]] = readConfig.map(validateValueE)

    val wrappedCompositionW: ReaderT[WriterW[Result], Int, Unit] = ReaderT(compositionW)
    val wrappedCompositionO: ReaderT[Option, Int, PositiveInt] = ReaderT(compositionO)
    val wrappedCompositionE: ReaderT[EitherE[String], Int, PositiveInt] = ReaderT(compositionE)
    val wrappedFunctionW = (u: Unit) => ReaderT(writerComputation(u))
    val wrappedFunctionO = (u: PositiveInt) => ReaderT(optionComputation(u))
    val wrappedFunctionE = (u: PositiveInt) => ReaderT(eitherComputation(u))
    val flatMappedCompositionW = wrappedCompositionW.flatMap(wrappedFunctionW)
    val flatMappedCompositionO = wrappedCompositionO.flatMap(wrappedFunctionO)
    val flatMappedCompositionE = wrappedCompositionE.flatMap(wrappedFunctionE)

    println(flatMappedCompositionW.value(2023))
    println(flatMappedCompositionO.value(2023))
    println(flatMappedCompositionO.value(-2023))
    println(flatMappedCompositionE.value(2023))
    println(flatMappedCompositionE.value(-2023))