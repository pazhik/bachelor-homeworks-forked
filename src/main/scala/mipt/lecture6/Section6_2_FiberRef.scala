package mipt.lecture6

import zio._

import java.io.IOException



object Section6_2_FiberRef extends ZIOAppDefault {

  private val tracksDone: UIO[Ref[Int]] = Ref.make(0)


  def heavyMetalCalculation(fiberRef: FiberRef[Int], refCounter: Ref[Int]) =
    fiberRef.locally(5) {
      for {
        _ <- Console.printLine("m ]-> starting")
        _ <- ZIO.sleep(Duration.fromMillis(1800))
        track <- fiberRef.get
        _ <- Console.printLine(s"m ]-> finishing $track")
        count <- refCounter.modify(prev => (prev+1, prev+1))
        _ <- Console.printLine(s"m ]-> counting $count")
      } yield "Warlord returns"
    }


  def hardRockWork(fiberRef: FiberRef[Int], refCounter: Ref[Int]) =
    for {
      _ <- Console.printLine("h -*> starting")
      _ <- ZIO.sleep(Duration.fromMillis(500))
      track <- fiberRef.modify(prev => (prev, prev+1))
      _ <- Console.printLine(s"h -*> finishing $track")
      count <- refCounter.modify(prev => (prev+1, prev + 1))
      _ <- Console.printLine(s"h -*> counting $count")
    } yield "Crazy train"


  def punkIsNotDead(fiberRef: FiberRef[Int], refCounter: Ref[Int]) =
    for {
      _ <- Console.printLine("p }*> starting")
      _ <- ZIO.sleep(Duration.fromMillis(3000))
      track <- fiberRef.modify(prev => (prev, prev+1))
      _ <- Console.printLine(s"p }*> finishing $track")
      count <- refCounter.modify(prev => (prev+1, prev + 1))
      _ <- Console.printLine(s"p }*> counting $count")
    } yield "Offspring" -> "Smash"



  val common =
    for {
      _ <- Console.printLine("\t> Starting")

      trackRef  <- FiberRef.make[Int](1)
      counter   <- tracksDone
      fiber1    <- heavyMetalCalculation(trackRef, counter).fork
      fiber2    <- hardRockWork(trackRef, counter).repeatN(3).fork
      fiber3    <- punkIsNotDead(trackRef, counter).fork

      _ <- Console.printLine("\t> Waiting")

      zips    = fiber1.zip(fiber2)
      result <- zips.orElse(fiber3).join

      count <- counter.get
      _ <- Console.printLine(s"\t> Completing with play track count = $count")
    } yield result




  override def run: ZIO[Scope, Any, Any] =
    common
      .tap(result => Console.printLine(result))

  }
