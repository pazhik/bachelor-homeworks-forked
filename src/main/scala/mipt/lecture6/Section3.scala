package mipt.lecture6


import java.io.IOException
import scala.util.Try


object Section3 extends App {




  //// Зоопарк
  object CosmoZoo {


    import Section2.InputOutputData.InOut


    // EitherProgram[R, E, A] extends Program[R, E, A]
    trait EitherProgram[R, E, A] {
      //  doSomeWork(runtimeEnvironment: R): InOut[Either[E, A]]
      def doSomeWork(runtimeEnvironment: R): InOut[Either[E, A]]
    }


    // TryProgram[R, A] extends Program[R, Throwable, A]
    trait TryProgram[R, A] {
      //  doSomeWork(runtimeEnvironment: R): InOut[Either[Throwable, A]]
      def doSomeWork(runtimeEnvironment: R): InOut[Try[A]]
    }


    // OptionProgram[R, A] extends Program[R, Nothing, A]
    trait OptionProgram[R, E, A] {
      //  doSomeWork(runtimeEnvironment: R): InOut[Either[Nothing, A]]
      def doSomeWork(runtimeEnvironment: R): InOut[Option[A]]
    }


    // В общем виде:
    trait ProgramF[F[_, _], R, E, A] {
      def doSomeWork(runtimeEnvironment: R): F[E, A]
    }


  }



  ///



  object InOutErrorAsResult {




    sealed trait InOutError[+E, +A] { self =>

      override def toString: String =
        self match {
          case InOutSuccess(value) =>
            s"InOutSuccess(${value()})"
          case InOutFailure(failure) =>
            s"InOutFailure(${failure()})"
        }

    }



    final case class InOutSuccess[A](value: () => A) extends InOutError[Nothing, A]
    final case class InOutFailure[E](error: () => E) extends InOutError[E, Nothing]



    /// R => InOutError[E, A]

    trait IOProgram[-R, +E, +A] extends (R => InOutError[E, A])



    /// Console  для нового InOutError

    final case class InOutFailedWith(error: String)


    trait InOutConsole[S] {
      def printLine(line: => S) : InOutError[InOutFailedWith, Unit]
      def readLine              : InOutError[InOutFailedWith, S]
    }


    ///


    implicit class InOutErrorOps[E, A](inOut: InOutError[E, A]) {
      def flatMap[B](f: A => InOutError[E, B]): InOutError[E, B] =
        inOut match {
          case InOutSuccess(value) =>
            f(value())
          case failure =>
            failure.asInstanceOf[InOutError[E, B]]
        }


      def map[B](f: A => B): InOutError[E, B] =
        inOut match {
          case InOutSuccess(value) =>
            InOutSuccess(() => f(value()))
          case failure =>
            failure.asInstanceOf[InOutError[E, B]]
        }

    }



    // Чего ещё душа изволит?
    // Чистого добра и зла!



    object InOutError {

      def pure[E, A](value: => A): InOutError[E, A] =
        InOutSuccess(() => value)

      def riseError[E, A](error: => E): InOutError[E, A] =
        InOutFailure(() => error)

    }



      // А для полного счастья чего не хватает?


      def filter = ???

      def withFilter = ???

      def foreach = ???



  }


  import InOutErrorAsResult._


  // Новая консоль

  object StringConsole extends InOutConsole[String] {

    override def printLine(line: => String): InOutError[InOutFailedWith, Unit] =
      InOutError.pure(println(line))

    override def readLine: InOutError[InOutFailedWith, String] =
      InOutError.pure(scala.io.StdIn.readLine())

  }



  val helloProgram: IOProgram[InOutConsole[String], InOutFailedWith, String] =
    new IOProgram[InOutConsole[String], InOutFailedWith, String] {
      override def apply(runtimeEnvironment: InOutConsole[String]): InOutError[InOutFailedWith, String] = {
        import runtimeEnvironment._
        for {
          _       <- printLine("Who are you?")
          name    <- readLine
          _       <- printLine("really?")
          ack     <- readLine
          result  <-
            ack.toLowerCase match {
              case "yes" =>
                InOutError.pure(name)
              case _ =>
                InOutError.riseError(InOutFailedWith("Inadequate"))
            }
          _   <- printLine(s"Hello, $name!")
        } yield result
      }
    }



  println("-" * 8)


  val result1 = helloProgram(StringConsole)
  println(result1)
  val result2 = helloProgram(StringConsole)
  println(result2)

  println()



  object LazyTillTheEndOfTheWorld {


    trait LazyInOutProgram[-R, +E, +A] extends (R => (() => InOutError[E, A]))


  }

  import LazyTillTheEndOfTheWorld._


  val lazyProgram: LazyInOutProgram[InOutConsole[String], InOutFailedWith, String] =
    new LazyInOutProgram[InOutConsole[String], InOutFailedWith, String] {
      override def apply(runtimeEnvironment: InOutConsole[String]): () => InOutError[InOutFailedWith, String] =
        () => {
          import runtimeEnvironment._
          for {
            _       <- printLine("Who are you?")
            name    <- readLine
            _       <- printLine("really?")
            ack     <- readLine
            result  <-
              ack.toLowerCase match {
                case "yes" =>
                  InOutError.pure(name)
                case _ =>
                  InOutError.riseError(InOutFailedWith("Inadequate"))
              }
            } yield result
        }
    }

  // Что нажа прога? Эффект!
  val recipe = lazyProgram(StringConsole)



  println("-" * 8)

  // Сколько раз исполняетм эффект, столько раз он выполняется полностью и выдвёт результат
  val result21 = recipe.apply()
  println(result21)
  val result22 = recipe()
  println(result22)

  println()






  // А ZIO выйдет погулять?





  import zio._


  val helloZio: ZIO[Any, IOException, String] =
    for {
      _     <- Console.printLine("Who are you?")
      name  <- Console.readLine
      ack   <- Console.readLine("Are you sure?")
      resp  <-
        ack.toLowerCase match {
          case "yes" =>
            ZIO.succeed(s"Hello, $name!")
          case _ =>
            ZIO.fail(new IOException("Inadequate"))
        }
    } yield resp


  val runtime = Runtime.default


  println("-" * 8)

  // Unsafe говорит о том, что мы привносим чистоту в "грязный" код.
  // то ест ьвнутри могут быт ьсайд-эффекты и прочее
  val result = Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.run( helloZio )
  }

  println(result)

  println()

}
