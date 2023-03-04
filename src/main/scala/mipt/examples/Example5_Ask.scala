package mipt.examples

import mipt.auxiliary.MonoidP
import mipt.monad.context.Ask
import mipt.monad.instances.{ReaderP, ReaderR, StateS, WriterP, WriterW}
import mipt.monad.instances.ReaderP.given
import mipt.monad.instances.StateP.given
import mipt.monad.transformers.{ReaderT, ReaderTF, StateT, StateTF, WriterT, WriterTF}
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*

object Question5_Ask:
  type Log
  given MonoidP[Log] = ???
  type StateValue

  val computation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Unit] = ???
  def read: ReaderP[Int, Int] = identity // How to apply to our computation?

  // Ugly solution
  val wrappedRead: ReaderT[StateTF[WriterW[Log], StateValue], Int, Int] =
    ReaderT(r => StateT(s => (r, s).pure[WriterW[Log]]))

  val chainedComputation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Int] = computation.flatMap(_ => wrappedRead)

object Example5_Ask:
  type Log = String
  type StateValue = Int

  @main def e5: Unit =
    val computation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Unit] =
      ReaderT(r => StateT(s => WriterP("I'm a very useful log", ((), s + r))))

    val askedComputation: ReaderT[StateTF[WriterW[Log], StateValue], Int, Int] =
      computation.flatMap(_ => Ask[ReaderTF[StateTF[WriterW[Log], StateValue], Int], Int].ask)

    println(askedComputation.value(2023).value(42))
