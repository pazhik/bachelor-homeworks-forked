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

object Question3_MonadComposition:
  type Result
  given MonoidP[Result] = ???

  def readConfig: ReaderP[Int, Int] = ???
  def proceedValue: Int => Result = ???
  def logResult: Result => WriterP[Result, Unit] = ???

  def anotherComputation: Unit => ReaderP[Int, WriterP[Result, Int]] = ??? // How to compose with our result???

case class ComposedMonad[F[_]: Monad, G[_]: Monad, A](value: F[G[A]])
type ComposedMonadFG[F[_], G[_]] = [A] =>> ComposedMonad[F, G, A]

object ComposedMonad:
  given [F[_]: Monad, G[_]: Monad]: Monad[ComposedMonadFG[F, G]] = new Monad[ComposedMonadFG[F, G]]:
    override def pure[A](a: A): ComposedMonad[F, G, A] = ComposedMonad(a.pure[G].pure[F])

    override def flatMap[A, B](fa: ComposedMonadFG[F, G][A])(f: A => ComposedMonadFG[F, G][B]): ComposedMonadFG[F, G][B] = ??? // We have some problems

case class ReaderWriter[R, W: MonoidP, A](value: R => WriterP[W, A])
type ReaderWriterRW[R, W] = [A] =>> ReaderWriter[R, W, A]

object ReaderWriter:
  given [R, W: MonoidP]: Monad[ReaderWriterRW[R, W]] = new Monad[ReaderWriterRW[R, W]]:
    override def pure[A](a: A): ReaderWriter[R, W, A] = ReaderWriter(_ => WriterP(MonoidP[W].empty, a))

    override def flatMap[A, B](fa: ReaderWriter[R, W, A])(f: A => ReaderWriter[R, W, B]): ReaderWriter[R, W, B] =
      ReaderWriter(r => for {
        a <- fa.value(r)
        b <- f(a).value(r)
      } yield b)

object Example3_MonadComposition:
  type Result = String

  def readConfig: ReaderP[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => WriterP[Result, Unit] = r => WriterP(r, ())

  def anotherComputation: Unit => ReaderP[Int, WriterP[Result, Int]] =
    (_: Unit) => {
      println("Yeah!")
      42
    }.pure[WriterW[Result]].pure[ReaderR[Int]]

  @main def e3: Unit =
    val composition: ReaderP[Int, WriterP[Result, Unit]] = readConfig.map(proceedValue).map(logResult)

    val wrappedComposition = ReaderWriter(composition)
    val wrappedFunction = (u: Unit) => ReaderWriter(anotherComputation(u))
    val flatMappedComposition = wrappedComposition.flatMap(wrappedFunction)

    println(flatMappedComposition.value(2023))

object Question3_1_ReaderOptionComposition:
  def readConfig: ReaderP[Int, Int] = ???
  def validateValue: Int => OptionP[Unit] = ???

  def anotherComputation: Unit => ReaderP[Int, OptionP[Int]] = ??? // How to compose with our result???

case class ReaderOption[R, A](value: R => OptionP[A])
type ReaderOptionR[R] = [A] =>> ReaderOption[R, A]

object ReaderOption:
  given [R]: Monad[ReaderOptionR[R]] = new Monad[ReaderOptionR[R]]:
    override def pure[A](a: A): ReaderOption[R, A] = ReaderOption(_ => SomeP(a))

    override def flatMap[A, B](fa: ReaderOption[R, A])(f: A => ReaderOption[R, B]): ReaderOption[R, B] =
      ReaderOption(r => for {
        a <- fa.value(r)
        b <- f(a).value(r)
      } yield b)

object Example3_1_MonadComposition:
  def readConfig: ReaderP[Int, Int] = identity
  def validateValue: Int => OptionP[Unit] = i => if i > 0 then SomeP(()) else NoneP

  def anotherComputation: Unit => ReaderP[Int, OptionP[Int]] =
    (_: Unit) => {
      println("Yeah!")
      42
    }.pure[OptionP].pure[ReaderR[Int]]

  @main def e3_1: Unit =
    val composition: ReaderP[Int, OptionP[Unit]] = readConfig.map(validateValue)

    val wrappedComposition = ReaderOption(composition)
    val wrappedFunction = (u: Unit) => ReaderOption(anotherComputation(u))
    val flatMappedComposition = wrappedComposition.flatMap(wrappedFunction)

    println(flatMappedComposition.value(2023))
    println(flatMappedComposition.value(-2023))

object Question3_2_ReaderEitherComposition:
  def readConfig: ReaderP[Int, Int] = ???
  def validateValue: Int => EitherP[String, Unit] = ???

  def anotherComputation: Unit => ReaderP[Int, EitherP[String, Int]] = ??? // How to compose with our result???

case class ReaderEither[R, E, A](value: R => EitherP[E, A]) // Something pretty familiar
type ReaderEitherRE[R, E] = [A] =>> ReaderEither[R, E, A]

object ReaderEither:
  given [R, E]: Monad[ReaderEitherRE[R, E]] = new Monad[ReaderEitherRE[R, E]]: // Looks the same as the previous ones
    override def pure[A](a: A): ReaderEither[R, E, A] = ReaderEither(_ => RightP(a))

    override def flatMap[A, B](fa: ReaderEither[R, E, A])(f: A => ReaderEither[R, E, B]): ReaderEither[R, E, B] =
      ReaderEither(r => for {
        a <- fa.value(r)
        b <- f(a).value(r)
      } yield b)

object Example3_2_MonadComposition:
  def readConfig: ReaderP[Int, Int] = identity
  def validateValue: Int => EitherP[String, Unit] = i => if i > 0 then RightP(()) else LeftP("Positive value expected")

  def anotherComputation: Unit => ReaderP[Int, EitherP[String, Int]] =
    (_: Unit) => {
      println("Yeah!")
      42
    }.pure[EitherE[String]].pure[ReaderR[Int]]

  @main def e3_2: Unit =
    val composition: ReaderP[Int, EitherP[String, Unit]] = readConfig.map(validateValue)

    val wrappedComposition = ReaderEither(composition)
    val wrappedFunction = (u: Unit) => ReaderEither(anotherComputation(u))
    val flatMappedComposition = wrappedComposition.flatMap(wrappedFunction)

    println(flatMappedComposition.value(2023))
    println(flatMappedComposition.value(-2023))