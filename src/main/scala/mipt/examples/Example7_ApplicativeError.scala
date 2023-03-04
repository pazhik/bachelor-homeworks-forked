package mipt.examples

import mipt.auxiliary.MonoidP
import mipt.monad.context.Tell
import mipt.monad.instances.{EitherP, ReaderP, ReaderR, StateS, WriterP, WriterW}
import mipt.monad.instances.ReaderP.given
import mipt.monad.instances.StateP.given
import mipt.monad.transformers.{EitherT, ReaderT, ReaderTF, StateT, StateTF, WriterT, WriterTF}
import mipt.monad.ApplicativeSyntax.*
import mipt.monad.FunctorSyntax.*
import mipt.monad.MonadSyntax.*
import mipt.monad.instances.EitherP.{LeftP, RightP}

import mipt.monad.ApplicativeErrorSyntax.*

object Question7_ApplicativeError:
  type Log
  given MonoidP[Log] = ???
  type StateValue

  val computation: EitherT[WriterTF[StateTF[ReaderR[Int], StateValue], Log], String, Int] = ??? // Let me make the example a bit harder
  def error: String = ??? // How to apply to our computation?

  // Ugly solution
  val wrappedError: EitherT[WriterTF[StateTF[ReaderR[Int], StateValue], Log], String, Unit] =
    EitherT(WriterT(StateT(s => _ => (WriterP(MonoidP[Log].empty, LeftP(error)), s)))) // Don't repeat my mistakes

  val chainedComputation: EitherT[WriterTF[StateTF[ReaderR[Int], StateValue], Log], String, Unit] =
    computation.flatMap(_ => wrappedError)

object Example7_ApplicativeError:
  type Log = String
  type StateValue = Int

  @main def e7: Unit =
    val computation: EitherT[WriterTF[StateTF[ReaderR[Int], StateValue], Log], String, Int] =
      EitherT(WriterT(StateT(s => r => (WriterP("I'm a very useful log", RightP(r)), r + s))))
    def error: String = "I'm an error"

    val erroredComputation: EitherT[WriterTF[StateTF[ReaderR[Int], StateValue], Log], String, Unit] =
      computation.flatMap(_ => error.raiseError)

    println(erroredComputation.value.value.value(42)(2023))
