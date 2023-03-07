package mipt.examples

import cats.{Monad, Monoid}
import mipt.monad.instances.{EitherE, Reader, ReaderR, Writer, WriterW}
import mipt.monad.instances.Reader.given
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

import scala.annotation.tailrec

object Question3_MonadComposition:
  type Result = String

  def readConfig: Reader[Int, Int] = ???
  def proceedValue: Int => Result = ???
  def logResult: Result => Writer[Result, Result] = ???

  def anotherComputation: Result => Reader[Int, Writer[Result, Int]] = ??? // How to compose with our result???

object ComposedMonad:
  given [F[_]: Monad, G[_]: Monad]: Monad[ComposedFG[F, G]] = new Monad[ComposedFG[F, G]]:
    override def pure[A](a: A): Composed[F, G, A] = Composed(a.pure[G].pure[F])

    override def flatMap[A, B](fa: ComposedFG[F, G][A])(f: A => ComposedFG[F, G][B]): ComposedFG[F, G][B] = ??? // We have some problems

    override def tailRecM[A, B](a: A)(f: A => ComposedFG[F, G][Either[A, B]]): ComposedFG[F, G][B] = ???

case class ReaderWriter[R, W: Monoid, A](value: R => Writer[W, A])
type ReaderWriterRW[R, W] = [A] =>> ReaderWriter[R, W, A]

object ReaderWriter:
  given [R, W: Monoid]: Monad[ReaderWriterRW[R, W]] = new Monad[ReaderWriterRW[R, W]]:
    override def pure[A](a: A): ReaderWriter[R, W, A] = ReaderWriter(_ => Writer(Monoid[W].empty, a))

    override def flatMap[A, B](fa: ReaderWriter[R, W, A])(f: A => ReaderWriter[R, W, B]): ReaderWriter[R, W, B] =
      ReaderWriter(r => for {
        a <- fa.value(r)
        b <- f(a).value(r)
      } yield b)

      /*
       fa: R => Writer[A]
       f: A => R => Writer[B]

        r =>
          val a = fa(r)
          val b = f(a)(r)
          b
       */

    override def tailRecM[A, B](a: A)(f: A => ReaderWriter[R, W, Either[A, B]]): ReaderWriter[R, W, B] =
      ReaderWriter(r => Monad[WriterW[W]].tailRecM(a)(a => f(a).value(r)))

object Example3_MonadComposition:
  type Result = String

  def readConfig: Reader[Int, Int] = identity
  def proceedValue: Int => Result = i => s"config number $i is readed"
  def logResult: Result => Writer[Result, Result] = r => Writer(r, r)

  def anotherComputation: Result => Reader[Int, Writer[Result, Int]] =
    r => r.length.pure[WriterW[Result]].pure[ReaderR[Int]]

  @main def e3: Unit =
    val composition: Reader[Int, Writer[Result, Result]] = readConfig.map(proceedValue).map(logResult)

    val wrappedComposition = ReaderWriter(composition)
    val wrappedFunction = (u: Result) => ReaderWriter(anotherComputation(u))
    val flatMappedComposition = wrappedComposition.flatMap(wrappedFunction)

    println(flatMappedComposition.value(2023))

object Question3_1_ReaderOptionComposition:
  type PositiveInt = Int

  def readConfig: Reader[Int, Int] = ???
  def validateValue: Int => Option[PositiveInt] = ???

  def anotherComputation: PositiveInt => Reader[Int, Option[Int]] = ??? // How to compose with our result???

case class ReaderOption[R, A](value: R => Option[A])
type ReaderOptionR[R] = [A] =>> ReaderOption[R, A]

object ReaderOption:
  given [R]: Monad[ReaderOptionR[R]] = new Monad[ReaderOptionR[R]]:
    override def pure[A](a: A): ReaderOption[R, A] = ReaderOption(_ => Some(a))

    override def flatMap[A, B](fa: ReaderOption[R, A])(f: A => ReaderOption[R, B]): ReaderOption[R, B] =
      ReaderOption(r => for {
        a <- fa.value(r)
        b <- f(a).value(r)
      } yield b)

    override def tailRecM[A, B](a: A)(f: A => ReaderOption[R, Either[A, B]]): ReaderOption[R, B] =
      ReaderOption(r => Monad[Option].tailRecM(a)(a => f(a).value(r)))

object Example3_1_MonadComposition:
  type PositiveInt = Int

  def readConfig: Reader[Int, Int] = identity
  def validateValue: Int => Option[PositiveInt] = i => if i > 0 then Some(i) else None

  def anotherComputation: PositiveInt => Reader[Int, Option[Int]] =
    pi => (pi + 42).pure[Option].pure[ReaderR[Int]]

  @main def e3_1: Unit =
    val composition: Reader[Int, Option[PositiveInt]] = readConfig.map(validateValue)

    val wrappedComposition = ReaderOption(composition)
    val wrappedFunction = (u: PositiveInt) => ReaderOption(anotherComputation(u))
    val flatMappedComposition = wrappedComposition.flatMap(wrappedFunction)

    println(flatMappedComposition.value(2023))
    println(flatMappedComposition.value(-2023))

object Question3_2_ReaderEitherComposition:
  type PositiveInt = Int

  def readConfig: Reader[Int, Int] = ???
  def validateValue: Int => Either[String, PositiveInt] = ???

  def anotherComputation: PositiveInt => Reader[Int, Either[String, Int]] = ??? // How to compose with our result???

case class ReaderEither[R, E, A](value: R => Either[E, A]) // Something pretty familiar
type ReaderEitherRE[R, E] = [A] =>> ReaderEither[R, E, A]

object ReaderEither:
  given [R, E]: Monad[ReaderEitherRE[R, E]] = new Monad[ReaderEitherRE[R, E]]: // Looks the same as the previous ones
    override def pure[A](a: A): ReaderEither[R, E, A] = ReaderEither(_ => Right(a))

    override def flatMap[A, B](fa: ReaderEither[R, E, A])(f: A => ReaderEither[R, E, B]): ReaderEither[R, E, B] =
      ReaderEither(r => for {
        a <- fa.value(r)
        b <- f(a).value(r)
      } yield b)

    override def tailRecM[A, B](a: A)(f: A => ReaderEither[R, E, Either[A, B]]): ReaderEither[R, E, B] =
      ReaderEither(r => Monad[EitherE[E]].tailRecM(a)(a => f(a).value(r)))

object Example3_2_MonadComposition:
  type PositiveInt = Int

  def readConfig: Reader[Int, Int] = identity
  def validateValue: Int => Either[String, PositiveInt] = i => if i > 0 then Right(i) else Left("Positive value expected")

  def anotherComputation: PositiveInt => Reader[Int, Either[String, Int]] =
    pi => (pi + 42).pure[EitherE[String]].pure[ReaderR[Int]]

  @main def e3_2: Unit =
    val composition: Reader[Int, Either[String, PositiveInt]] = readConfig.map(validateValue)

    val wrappedComposition = ReaderEither(composition)
    val wrappedFunction = (u: PositiveInt) => ReaderEither(anotherComputation(u))
    val flatMappedComposition = wrappedComposition.flatMap(wrappedFunction)

    println(flatMappedComposition.value(2023))
    println(flatMappedComposition.value(-2023))
