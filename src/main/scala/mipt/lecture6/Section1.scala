package mipt.lecture6

import scala.util.Try



object Section1 extends App {

  object Dirty {


    trait Generator[A] {
      def doSomeWork: () => A
    }


    class StringGenerator extends Generator[String] {
      private def work(): String = {
        Thread.sleep(scala.util.Random.nextInt(100))
        s"Genarate value ${scala.util.Random.nextInt()} at ${java.time.Clock.systemUTC().millis()}"
      }

      override val doSomeWork: () => String =
        work
    }


  }

  import Dirty._





  // Объект, каким-то волшебным образом генерирует String-и
  // возможно читает из файла, сокета, консоли, базы или ещё откуда-то
  val dirtyGenerator: Generator[String] = new StringGenerator

  println("-" * 8)

  // Например:
  println(dirtyGenerator.doSomeWork())
  println(dirtyGenerator.doSomeWork())
  println(dirtyGenerator.doSomeWork())
  // Как такое тестировать?

  println()







  ///








  object Failable {


    trait Translator[R, A] {
      def doSomeWork: R => A
    }


    object IntToDoubleTranslator extends Translator[Int, Double] {
      private def work(divisor: Int): Double =
        (100 % divisor) + (100 / divisor.toDouble)

      override val doSomeWork: Int => Double =
        work
    }


  }

  import Failable._





  // Объект, каким-то волшебным образом превращает Int в Double
  // Ура! Выход однозначно зависит от входа.
  val failableTranslator: Translator[Int, Double] = IntToDoubleTranslator

  println("-" * 8)

  // Например:
  println(Try(failableTranslator.doSomeWork(123)))
  println(Try(failableTranslator.doSomeWork(0)))
  println(Try(failableTranslator.doSomeWork(-123)))
  // Что говорит контракт метода об ошибках?
  // Надо ли помнить о том, что что-то пойти е так?

  println()







  ///








  object Complete {


    trait Calculation[R, E, A] {
      def doSomeWork: R => Either[E, A]
    }


    def makePositiveCalculator =
      new Calculation[Int, Exception, Double] {

        private def work(divisor: Int): Either[Exception, Double] =
          divisor match {
            case 0 =>
              Left(new IllegalArgumentException("By Zero!!!"))
            case d if d < 0 =>
              Left(new IllegalStateException("Not implemented yet."))
            case d =>
              Right(1 / d.toDouble)
          }

        override def doSomeWork: Int => Either[Exception, Double] =
          work

      }


  }

  import Complete._





  // Объект, каким-то волшебным образом превращает Int в Double
  // Ура! Выход однозначно зависит от входа.
  // Ура! Мы можем получить или результат или ошибку.
  val someCalculator: Calculation[Int, Exception, Double] = makePositiveCalculator

  
  println("-" * 8)

  // Например:
  println(someCalculator.doSomeWork(123))
  println(someCalculator.doSomeWork(0))
  println(someCalculator.doSomeWork(-123))
  // Чистота функций - залог психического здоровья при тестировании!

  println()







  ///








  object InputOutput {


    trait Program[R, E, A] {
      def doSomeWork(runtimeEnvironment: R): Either[E, A]
    }

    ///


    sealed trait Input[In] {
      def readLine: () => In
    }

    sealed trait Output[Out] {
      def printLine: Out => Unit
    }

    ///


    object Console extends Input[String] with Output[String] {

      override val readLine: () => String =
        () => scala.io.StdIn.readLine()

      override val printLine: String => Unit =
        println

    }




    object Mock extends Input[String] with Output[String] {
      import java.util.concurrent.atomic.AtomicReference
      private val inputs = new AtomicReference[List[String]](List("Human", "yes", "Pretty kitten", "meow!"))

      override val readLine: () => String =
        () => inputs.get match {
          case head :: tail =>
            inputs.set(tail ::: head :: Nil)
            head
          case _ =>
            ""
        }

      override val printLine: String => Unit =
        _ => ()

    }


  }

  import InputOutput._





  // Объъект читает из консоли, пишет в консоль, принимает какое-то решение и возращает какой-то String или ошибку
  val helloProgram = new Program[Input[String] with Output[String], Throwable, String] {

    override def doSomeWork(runtimeEnvironment: Input[String] with Output[String]): Either[Throwable, String] = {

      import runtimeEnvironment._

      printLine.apply("Who are you?")
      val name = readLine.apply()

      printLine("really?")
      val ack = readLine()

      ack.toLowerCase match {
        case "yes" =>
          Right(name)
        case _ =>
          Left(new Exception("Inadequate"))
      }
    }
  }


  println("-" * 8)

  val appResult1 = helloProgram.doSomeWork(Mock)
  println(appResult1)
  val appResult2 = helloProgram.doSomeWork(Mock)
  println(appResult2)

  println()
  
  
}
