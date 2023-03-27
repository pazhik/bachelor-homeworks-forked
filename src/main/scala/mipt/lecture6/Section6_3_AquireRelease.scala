package mipt.lecture6

import zio._



object Section6_3_AquireRelease extends ZIOAppDefault {

  val fromBasement =
    Console
      .printLine("Hoy!")
      .ignore


  val zombies =
    for {
      _ <- Console.printLine("revive")
      _ <- Console.printLine("alive!")
      _ <- Console.printLine("All night long")
      _ <- ZIO.sleep(Duration.fromMillis(30))
//      _ <- ZIO.fail("Sun is rising!")
      _ <- Console.printLine("All day long")
    } yield "dance"



 override def run: ZIO[Any, Any, Any] =
   zombies
     .ensuring(fromBasement)
     .foldZIO(
       failure => Console.printLine(failure),
       success => Console.printLine(success).repeatN(2)
     )



  ///



  val safeZombies =
    ZIO.acquireReleaseInterruptible(
      Console.printLine("revive")
        *> ZIO.succeed("undead")
    )(
      //zombie =>
        Console
          .printLine("zombie burn")
          .ignore
    )

//
//  override def run: ZIO[Scope, Any, Any] =
//    safeZombies
//      .tap(result => Console.printLine(result))





//
//  override def run: ZIO[Scope, Any, Any] =
//
//    ZIO.scoped {
//      safeZombies
//        .tap(result => Console.printLine(result))
//    }



  ///



  val deadAnarchist =
    ZIO.acquireReleaseWith(
      Console.printLine("revive")
        *> ZIO.succeed("Dead Artist")
    )(
      zombie =>
        Console
          .printLine(s"$zombie says: Hoy!")
          .ignore
    )(
      zombie =>
        Console
          .printLine(s"$zombie dances all night long")
          *> ZIO.succeed("hidden zombie")
          <* ZIO.fail("oups!")
    )


  // override def run: ZIO[Any, Any, Any] =
  //   deadAnarchist
  //     .tap(result => Console.printLine(result))


}


