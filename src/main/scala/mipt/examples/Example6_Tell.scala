package mipt.examples

import mipt.monad.context.Tell
import mipt.monad.instances.{Reader, ReaderR, StateS, Writer, WriterW}
import mipt.monad.instances.Reader.given
import mipt.monad.instances.State.given
import mipt.monad.transformers.{ReaderT, ReaderTF, StateT, StateTF, WriterT, WriterTF}

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*
import mipt.monad.context.TellSyntax.*

object Question6_Tell:
  type Log = String
  type StateValue

  val computation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Log] = ???
  def write: Log => Writer[Log, Unit] = l => Writer(l, ()) // How to apply to our computation?

  // Ugly solution
  val wrappedRead: Log => WriterT[StateTF[ReaderR[Int], StateValue], Log, Unit] =
    l => WriterT(StateT(s => _ => (Writer(l, ()), s)))

  val chainedComputation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Unit] = computation.flatMap(wrappedRead)

object Example6_Tell:
  type Log = String
  type StateValue = Int

  @main def e6: Unit =
    val computation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Log] =
      WriterT(StateT(s => r => (Writer("", "I'm a very useful log"), s + r)))

    val telledComputation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Unit] =
      computation.flatMap(_.tell)

    println(telledComputation.value.value(42)(2023))
