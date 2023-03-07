package mipt.examples

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

  @main def e5: Unit =
    type OurMonad[A] = ReaderTF[StateTF[WriterW[Log], StateValue], Int][A]

    val computation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Unit] =
      ReaderT(r => StateT(s => Writer("I'm a very useful log", ((), s + r))))

    val askedComputation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Int] =
      computation.flatMap(_ => Ask[OurMonad, Int].ask)

    println(askedComputation.value(2023).value(42))
