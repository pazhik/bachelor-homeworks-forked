package mipt.examples

import cats.Functor
import mipt.monad.context.Ask
import mipt.monad.instances.{Reader, ReaderR, StateS, Writer, WriterW}
import mipt.monad.instances.Reader.given
import mipt.monad.instances.State.given
import mipt.monad.transformers.{ReaderT, ReaderTF, StateT, StateTF, WriterT, WriterTF}
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

object Question5_Ask:
  type Log = String
  type StateValue

  val computation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Unit] = ???
  def read: Reader[Int, Int] = identity // How to apply to our computation?

  // Ugly solution
  val wrappedRead: ReaderT[StateTF[WriterW[Log], StateValue], Int, Int] =
    ReaderT(r => StateT(s => (r, s).pure[WriterW[Log]]))

  val chainedComputation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Int] = computation.flatMap(_ => wrappedRead)

object Example5_Ask:
  type Log = String
  type StateValue = Int

  type OurMonad[A] = ReaderTF[StateTF[WriterW[Log], StateValue], Int][A]

  @main def e5: Unit =
    val computation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Unit] =
      ReaderT(r => StateT(s => Writer("I'm a very useful log", ((), s + r))))

    val askedComputation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Int] =
      computation.flatMap(_ => Ask[OurMonad, Int].ask)

    println(askedComputation.value(2023).value(42))

  trait ContextValidator[F[_], A]:
    def getValidatedContext: F[Option[A]]

  object ContextValidator:
    def apply[F[_]: Functor, A](validation: A => Option[A])(using Ask[F, A]): ContextValidator[F, A] =
      new Impl[F, A](validation)

    class Impl[F[_]: Functor, A](validation: A => Option[A])(using Ask[F, A]) extends ContextValidator[F, A]:
      override def getValidatedContext: F[Option[A]] = Ask[F, A].ask.map(validation)

  @main def e5_1: Unit =
    val cv: ContextValidator[ReaderR[Int], Int] = ContextValidator(i => if (i > 0) then Some(i) else None)

    println(cv.getValidatedContext(2023))
    println(cv.getValidatedContext(-2023))

    val cv1: ContextValidator[OurMonad, Int] = ContextValidator(i => if (i > 0) then Some(i) else None)

    println(cv1.getValidatedContext.value(2023).value(42))
    println(cv1.getValidatedContext.value(-2023).value(42))

    given Ask[StateTF[ReaderR[Int], Int], Int] = new Ask:
      override def ask: StateT[ReaderR[Int], Int, Int] = StateT.fromFA(Ask[ReaderR[Int], Int].ask)
