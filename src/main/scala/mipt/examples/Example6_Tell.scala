package mipt.examples

import mipt.auxiliary.MonoidP
import mipt.monad.context.Tell
import mipt.monad.instances.{ReaderP, ReaderR, StateS, WriterP, WriterW}
import mipt.monad.instances.ReaderP.given
import mipt.monad.instances.StateP.given
import mipt.monad.transformers.{ReaderT, ReaderTF, StateT, StateTF, WriterT, WriterTF}

import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*
import mipt.monad.context.TellSyntax.*

object Question6_Ask:
  type Log
  given MonoidP[Log] = ???
  type StateValue

  val computation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Log] = ???
  def write: Log => WriterP[Log, Unit] = l => WriterP(l, ()) // How to apply to our computation?

  // Ugly solution
  val wrappedRead: Log => WriterT[StateTF[ReaderR[Int], StateValue], Log, Unit] =
    l => WriterT(StateT(s => _ => (WriterP(l, ()), s)))

  val chainedComputation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Unit] = computation.flatMap(wrappedRead)

object Example6_Ask:
  type Log = String
  type StateValue = Int

  @main def e6: Unit =
    val computation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Log] =
      WriterT(StateT(s => r => (WriterP("", "I'm a very useful log"), s + r)))

    val telledComputation: WriterT[StateTF[ReaderR[Int], StateValue], Log, Unit] =
      computation.flatMap(_.tell)

    println(telledComputation.value.value(42)(2023))
