package mipt.lecture6

import zio._
import zio.ZLayer.Debug

import scala.annotation.implicitNotFound



object Section5z extends ZIOAppDefault {


  object WillFailInRuntime {



    final case class Circuit[Payload](payload: Payload, info: String = "")


    trait CircuitBuilder[Payload] {
      def withInfo(info: String)    : CircuitBuilder[Payload]
      def withPayload[P](payload: P): CircuitBuilder[P]
      def build: Circuit[Payload]
    }


    private class EmptyBuilder(info: String) extends CircuitBuilder[Nothing] {
      override def withInfo(newInfo: String): CircuitBuilder[Nothing] =
        new EmptyBuilder(newInfo)

      override def withPayload[P](payload: P): CircuitBuilder[P] =
        new NonEmptyBuilder(info, payload)

      override def build: Circuit[Nothing] =
        ???
    }


    private class NonEmptyBuilder[Payload](info: String, payload: Payload) extends CircuitBuilder[Payload] {
      override def withInfo(newInfo: String): CircuitBuilder[Payload] =
        new NonEmptyBuilder(newInfo, payload)

      override def withPayload[P](payload: P): CircuitBuilder[P] =
        new NonEmptyBuilder(info, payload)

      override def build: Circuit[Payload] =
        Circuit(payload, info)
    }


    def makeBuilder: CircuitBuilder[Nothing] =
      new EmptyBuilder("")

  }


  override def run: ZIO[Any, Any, Any] =
    ZIO.logInfo(
      WillFailInRuntime.makeBuilder.withInfo("Some info").withPayload("Some payload").build.toString
    )



  object WillFailInCompileTime {
    import WillFailInRuntime.Circuit



    sealed trait PayloadEvidence

    trait PayloadPresent extends PayloadEvidence
    trait PayloadAbsent extends PayloadEvidence




    @implicitNotFound("CircuitBuilder requires payload defined. Please use withPayload method")
    trait Prove[T]
    implicit val payloadProve: Prove[PayloadPresent] = new Prove[PayloadPresent]{}


    trait CircuitBuilder[Payload, HasOutput <: PayloadEvidence] {

      def withInfo(info: String): CircuitBuilder[Payload, HasOutput]

      def withPayload[P](payload: P): CircuitBuilder[P, PayloadPresent]

      def build(implicit ie: Prove[HasOutput]): Circuit[Payload]

    }





    private class EmptyBuilder(info: String) extends CircuitBuilder[Nothing, PayloadAbsent] {

      override def withInfo(newInfo: String): CircuitBuilder[Nothing, PayloadAbsent] =
        new EmptyBuilder(newInfo)

      override def withPayload[P](payload: P): CircuitBuilder[P, PayloadPresent] =
        new NonEmptyBuilder(info, payload)

      override def build(implicit ie: Prove[PayloadAbsent]): Circuit[Nothing] =
        ???

    }





    private class NonEmptyBuilder[Payload](info: String, payload: Payload) extends CircuitBuilder[Payload, PayloadPresent] {

      override def withInfo(newInfo: String): CircuitBuilder[Payload, PayloadPresent] =
        new NonEmptyBuilder(newInfo, payload)

      override def withPayload[P](payload: P): CircuitBuilder[P, PayloadPresent] =
        new NonEmptyBuilder(info, payload)

      override def build(implicit ie: Prove[PayloadPresent]): Circuit[Payload] =
        Circuit(payload, info)

    }



    def makeBuilder: CircuitBuilder[Nothing, PayloadAbsent] =
      new EmptyBuilder("")

  }


//
//  override def run: ZIO[Any, Any, Any] =
//    ZIO.logInfo(
//      WillFailInCompileTime.makeBuilder.withInfo("Some info").withPayload("Some payload").build.toString
//    )




  object DependancyDay {



    trait Generator[A] {
      def generate: A
    }

    trait Modifier[A, B] {
      def modify(a: A): B
    }

    trait Consumer[B] {
      def consume(b: B): ZIO[Any, Throwable, Unit]
    }



    object Instances {
      val stringGenerator: Generator[String] =
        new Generator[String] {
            override def generate: String =
              "1-2-3-4-5-6-7-8-9-0"
        }


      def stringToIntModofier(payload: String => Int): Modifier[String, Int] =
        new Modifier[String, Int]{
          override def modify(string: String): Int =
            payload(string)
        }



      val intConsumer: UIO[Consumer[Int]] =
        ZIO.succeed(
          new Consumer[Int] {
            override def consume(int: Int): ZIO[Any, Throwable, Unit] =
              Console.printLine(s"consumed $int chars")
          }
        )
    }



  }


  import DependancyDay._, DependancyDay.Instances._



  val generatorLevel: ULayer[Generator[String]] =
    ZLayer.succeed(stringGenerator)

  val modifierLevel: ZLayer[String => Int, Nothing, Modifier[String, Int]] =
    ZLayer.fromFunction(stringToIntModofier _)

  val modifierPayloadLayer: ULayer[String => Int] =
    ZLayer.succeed((string: String) => string.length)

  val consumerLayer: ZLayer[Any, Nothing, Consumer[Int]] =
    ZLayer.fromZIO(intConsumer)


  val program: ZIO[Consumer[Int] & Modifier[String, Int] & Generator[String], Throwable, Unit] =
    for {
      gen <- ZIO.service[Generator[String]]
      mod <- ZIO.service[Modifier[String, Int]]
      out <- ZIO.service[Consumer[Int]]
      _ <- out.consume(mod.modify(gen.generate))
    } yield ()


  // override def run: ZIO[Any, Any, ExitCode] =
  //   program
  //     .provide(
  //       generatorLevel,
  //       modifierLevel,
  //       modifierPayloadLayer,
  //       consumerLayer,
  //       Debug.mermaid
  //     )
  //     .exitCode

}
