package mipt.examples

import mipt.auxiliary.MonoidP
import mipt.monad.Monad
import mipt.monad.instances.{EitherE, EitherP, OptionP, ReaderP, ReaderR, WriterP, WriterW}
import mipt.monad.instances.OptionP.{NoneP, SomeP}
import mipt.monad.instances.ReaderP.given
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*
import mipt.monad.instances.EitherP.{LeftP, RightP}
import mipt.monad.transformers.ReaderT

object Example4_MonadTransformers:
  type Result = String

  def readConfig: ReaderP[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => WriterP[Result, Unit] = r => WriterP(r, ())

  def writerComputation: Unit => ReaderP[Int, WriterP[Result, Int]] =
    (_: Unit) => {
      println("Yeah!")
      42
    }.pure[WriterW[Result]].pure[ReaderR[Int]]

  def validateValueO: Int => OptionP[Unit] = i => if i > 0 then SomeP(()) else NoneP

  def optionComputation: Unit => ReaderP[Int, OptionP[Int]] =
    (_: Unit) => {
      println("Yeah!")
      42
    }.pure[OptionP].pure[ReaderR[Int]]

  def validateValueE: Int => EitherP[String, Unit] = i => if i > 0 then RightP(()) else LeftP("Positive value expected")

  def eitherComputation: Unit => ReaderP[Int, EitherP[String, Int]] =
    (_: Unit) => {
      println("Yeah!")
      42
    }.pure[EitherE[String]].pure[ReaderR[Int]]

  @main def e4: Unit =
    val compositionW: ReaderP[Int, WriterP[Result, Unit]] = readConfig.map(proceedValue).map(logResult)
    val compositionO: ReaderP[Int, OptionP[Unit]] = readConfig.map(validateValueO)
    val compositionE: ReaderP[Int, EitherP[String, Unit]] = readConfig.map(validateValueE)

    val wrappedCompositionW: ReaderT[WriterW[Result], Int, Unit] = ReaderT(compositionW)
    val wrappedCompositionO: ReaderT[OptionP, Int, Unit] = ReaderT(compositionO)
    val wrappedCompositionE: ReaderT[EitherE[String], Int, Unit] = ReaderT(compositionE)
    val wrappedFunctionW = (u: Unit) => ReaderT(writerComputation(u))
    val wrappedFunctionO = (u: Unit) => ReaderT(optionComputation(u))
    val wrappedFunctionE = (u: Unit) => ReaderT(eitherComputation(u))
    val flatMappedCompositionW = wrappedCompositionW.flatMap(wrappedFunctionW)
    val flatMappedCompositionO = wrappedCompositionO.flatMap(wrappedFunctionO)
    val flatMappedCompositionE = wrappedCompositionE.flatMap(wrappedFunctionE)

    println(flatMappedCompositionW.value(2023))
    println(flatMappedCompositionO.value(2023))
    println(flatMappedCompositionO.value(-2023))
    println(flatMappedCompositionE.value(2023))
    println(flatMappedCompositionE.value(-2023))