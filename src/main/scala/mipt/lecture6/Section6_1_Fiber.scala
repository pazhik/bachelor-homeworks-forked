package mipt.lecture6


import zio.{Console, Duration, Fiber, ZIO, ZIOAppDefault}

import java.io.IOException



object Section6_1_Fiber extends ZIOAppDefault {
  val heavyMetalCalculation: ZIO[Any, IOException, String] =
    for {
      _ <- Console.printLine("]-> starting")
      _ <- ZIO.sleep(Duration.fromMillis(1800))
      _ <- Console.printLine("]-> finishing")
    } yield "Warlord returns"


  val hardRockWork =
    for {
      _ <- Console.printLine("-*> starting")
      _ <- ZIO.sleep(Duration.fromMillis(500))
      _ <- Console.printLine("-*> finishing")
    } yield "Crazy train"


  val punkIsNotDead =
    for {
      _ <- Console.printLine("}*> starting")
      _ <- ZIO.sleep(Duration.fromMillis(3000))
      _ <- Console.printLine("}*> finishing")
    } yield "Offspring" -> "Smash"

  val common =
    for {
      _ <- Console.printLine("Starting")

      //      fiberMetal: Fiber[IOException, Int] <- heavyMetalCalculation.fork
      fiberMetal <- heavyMetalCalculation.fork
      fiberRocky <- hardRockWork.repeatN(3).fork
      fiberPunk  <- punkIsNotDead.fork

      _ <- Console.printLine("Waiting")

      metal <- fiberMetal.join
      rocky <- fiberRocky.await

      punkStatus  <- fiberPunk.status
      _           <- Console.printLine(s"Punk is $punkStatus")
      _           <- ZIO.sleep(Duration.fromMillis(100))
      punks       <- fiberPunk.interrupt

      _ <- Console.printLine("Completing")
    } yield s"$metal with $rocky and $punks"


//  override def run: ZIO[Any, Any, Any] =
//    common
//      .tap(result => Console.printLine(result))

  val common2 =
    for {
      _ <- Console.printLine("Starting")

      //      fiberMetal: Fiber[IOException, Int] <- heavyMetalCalculation.fork
      fiber1 <- heavyMetalCalculation.fork
      fiber2 <- hardRockWork.repeatN(3).fork
      fiber3 <- punkIsNotDead.fork

      _ <- Console.printLine("Waiting")

      zips = fiber1.zip(fiber2)
      fiber = zips.orElse(fiber3)

      _ <- ZIO.sleep(Duration.fromMillis(1000))
      _ <- fiber3.interrupt
      result <- fiber.join

      _ <- Console.printLine("Completing")
    } yield result



  override def run: ZIO[Any, Any, Any] =
    common2
      .tap(result => Console.printLine(s"${result._1} with ${result._2}"))

}

