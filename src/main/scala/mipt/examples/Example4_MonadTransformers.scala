package mipt.examples

import cats.{Functor, Id, Monad}
import mipt.monad.instances.{EitherE, Reader, ReaderR, State, StateS, Writer, WriterW}
import mipt.monad.instances.Reader.given
import mipt.monad.instances.State.given
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*
import mipt.monad.transformers.{EitherT, ReaderT, StateT}

object Example4_MonadTransformers:
  type Result = String
  type PositiveInt = Int

  def readConfig: Reader[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => Writer[Result, Result] = r => Writer(r, r)

  def writerComputation: Result => Reader[Int, Writer[Result, Int]] =
    r => r.length.pure[WriterW[Result]].pure[ReaderR[Int]]

  def validateValueO: Int => Option[PositiveInt] = i => if i > 0 then Some(i) else None

  def optionComputation: PositiveInt => Reader[Int, Option[Int]] =
    pi => (pi + 42).pure[Option].pure[ReaderR[Int]]

  def validateValueE: Int => Either[String, PositiveInt] = i => if i > 0 then Right(i) else Left("Positive value expected")

  def eitherComputation: PositiveInt => Reader[Int, Either[String, Int]] =
    pi => (pi + 42).pure[EitherE[String]].pure[ReaderR[Int]]

  @main def e4: Unit =
    val compositionW: Reader[Int, Writer[Result, Result]] = readConfig.map(proceedValue).map(logResult)
    val compositionO: Reader[Int, Option[PositiveInt]] = readConfig.map(validateValueO)
    val compositionE: Reader[Int, Either[String, PositiveInt]] = readConfig.map(validateValueE)

    val wrappedCompositionW: ReaderT[WriterW[Result], Int, Result] = ReaderT(compositionW)
    val wrappedCompositionO: ReaderT[Option, Int, PositiveInt] = ReaderT(compositionO)
    val wrappedCompositionE: ReaderT[EitherE[String], Int, PositiveInt] = ReaderT(compositionE)
    val wrappedFunctionW = (u: Result) => ReaderT(writerComputation(u))
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

  @main def e4_1: Unit =
    val increaseState: Int => State[Int, Int] = a => s => (s, s + a)
    val validatePositive: Int => Either[String, Int] = i => if (i > 0) then Right(i) else Left("Positive value expected")

    val composition1: Int => State[Int, Either[String, Int]] =
      i => Functor[StateS[Int]].fmap(increaseState(i))(validatePositive)
    val composition2: Int => Int => Either[String, (Int, Int)] =
      i => s => validatePositive(i).map(increaseState).map(_(s))

    //                                   Int => (Either[String, Int], Int)
    val wrappedComposition1: Int => EitherT[StateS[Int], String, Int] = i => EitherT(composition1(i))
    //                                   Int => Either[String, (Int, Int)]
    val wrappedComposition2: Int => StateT[EitherE[String], Int, Int] = i => StateT(composition2(i))

    println(wrappedComposition1(-42).value(-2023))
    println(wrappedComposition2(-42).value(-2023))

  trait Checker[F[_]]:
    def checkInt(i: Int): F[Either[String, Int]]
    def checkString(s: String): F[Either[String, String]]
    def checkPair(is: (Int, String)): F[Either[String, (Int, String)]]

  object Checker:
    def apply[F[_]: Monad](
                            checkInt: Int => F[Either[String, Int]],
                            checkString: String => F[Either[String, String]]
                          ): Checker[F] = new Impl(checkInt, checkString)

    class Impl[F[_]: Monad](intChecker: Int => F[Either[String, Int]], stringChecker: String => F[Either[String, String]])
      extends Checker[F]:
      override def checkInt(i: Int): F[Either[String, Int]] = intChecker(i)

      override def checkString(s: String): F[Either[String, String]] = stringChecker(s)

      override def checkPair(is: (Int, String)): F[Either[String, (Int, String)]] =
        (for {
          i <- EitherT(checkInt(is._1))
          s <- EitherT(checkString(is._2))
        } yield (i, s)).value

  @main def e4_2: Unit =
    val checker: Checker[Id] =
      Checker(
        i => if (i > 0) then Right(i) else Left("Positive value expected"),
        s => if (s.isEmpty) then Left("Value cannot be empty") else Right(s)
      )

    println(checker.checkPair((0, "")))
    println(checker.checkPair((0, "some string")))
    println(checker.checkPair((42, "")))
    println(checker.checkPair((42, "some string")))
