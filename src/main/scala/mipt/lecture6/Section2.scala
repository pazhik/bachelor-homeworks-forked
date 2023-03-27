package mipt.lecture6





object Section2 extends App {




  trait Program[R, E, A] {
    def doSomeWork(runtimeEnvironment: R): Either[E, A]
  }




  /// Действие как объект, код как данные.




  object InputOutputData {


    // Завернём действие в case class
    final case class InOut[A](action: () => A)


    trait InOutConsole[S] {

      def printLine(line: => S): InOut[Unit]

      def readLine: InOut[S]

    }


    object InOutConsoleImpl extends InOutConsole[String] {

      // Декларативный стиль - это отсылка к первой лекции семестра

      def printLine(line: => String): InOut[Unit] =
        InOut(() => println(line))

      def readLine: InOut[String] =
        InOut(() => scala.io.StdIn.readLine())

    }


  }


  import InputOutputData._







  val helloProgram = new Program[InOutConsole[String], Throwable, String] {

    override def doSomeWork(runtimeEnvironment: InOutConsole[String]): Either[Throwable, String] = {
      import runtimeEnvironment._

      printLine("Who are you?").action()
      val name = readLine.action()

      printLine("really?").action()
      val ack = readLine.action()

      ack.toLowerCase match {
        case "yes" =>
          Right(name)
        case _ =>
          Left(new Exception("Inadequate"))
      }
    }

  }


  println("-" * 8)


 val appResult1 = helloProgram.doSomeWork(InOutConsoleImpl)
 println(appResult1)
 val appResult2 = helloProgram.doSomeWork(InOutConsoleImpl)
 println(appResult2)

  println()




  // это как-то очень сложно. Давайте упрощать




  object InputOutputComprehension {


    implicit class InOutOps[A](inOut: InOut[A]) {
      def flatMap[B](f: A => InOut[B]): InOut[B] =
        InOut(
          () => {
            val src = inOut.action()
            val dst = f(src)
            dst.action()
          }
        )

      def map[B](f: A => B): InOut[B] =
        InOut(
          () => {
            val src = inOut.action()
            val dst = f(src)
            dst
          }
        )
    }

  }


  import InputOutputComprehension._


  val helloProgram2 = new Program[InOutConsole[String], Throwable, String] {

    override def doSomeWork(runtimeEnvironment: InOutConsole[String]): Either[Throwable, String] = {
      import runtimeEnvironment._

      val recipe =
        for {
          _     <- printLine("Who are you?")
          name  <- readLine
          _     <- printLine("really?")
          ack   <- readLine
        } yield
          ack.toLowerCase match {
            case "yes" =>
              Right(name)
            case _ =>
              Left(new Exception("Inadequate"))
          }

      recipe.action()
    }

  }



  println("-" * 8)


  val appResult21 = helloProgram2.doSomeWork(InOutConsoleImpl)
  println(appResult21)
  val appResult22 = helloProgram2.doSomeWork(InOutConsoleImpl)
  println(appResult22)

  println()






  // Ещё немного меньше кода и больше изящества в теле программы
  object RecipeAsResult {


    trait IOProgram[R, E, A] {
      def doSomeWork(runtimeEnvironment: R): InOut[Either[E, A]]
    }


  }


  import RecipeAsResult._





  val helloProgram3: IOProgram[InOutConsole[String], Throwable, String] =
    new IOProgram[InOutConsole[String], Throwable, String] {
      override def doSomeWork(runtimeEnvironment: InOutConsole[String]): InOut[Either[Throwable, String]] = {
        import runtimeEnvironment._
        for {
          _     <- printLine("Who are you?")
          name  <- readLine
          _     <- printLine("really?")
          ack   <- readLine
        } yield
          ack.toLowerCase match {
            case "yes" =>
              Right(name)
            case _ =>
              Left(new Exception("Inadequate"))
          }
      }

    }


  println("-" * 8)

  val appResult31 = helloProgram3.doSomeWork(InOutConsoleImpl)
  println(appResult31.action())
  val appResult32 = helloProgram3.doSomeWork(InOutConsoleImpl)
  println(appResult32.action())

  println()




  println("-" * 8)

  val x = appResult31.flatMap(_ => appResult32)

  x.action()

  println()


}

