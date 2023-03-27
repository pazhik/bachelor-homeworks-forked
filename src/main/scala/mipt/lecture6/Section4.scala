package mipt.lecture6

import mipt.lecture6.Section3.InOutErrorAsResult.InOutFailedWith
import zio._

import java.io.IOException


object Section4 extends ZIOAppDefault {



  val helloZio: ZIO[Any, Exception, String] =
    for {
      _     <- Console.printLine("Who are you?")
      name  <- Console.readLine
      ack   <- Console.readLine("Are you sure?")
      resp  <-
        ack.toLowerCase match {
          case "yes" =>
            ZIO.succeed(name)
          case _ =>
            ZIO.fail(new Exception("Inadequate"))
        }
    } yield resp




  override def run: ZIO[Scope & ZIOAppArgs, Any, ExitCode] =
      helloZio
//        .fold(
//          failure =
//            _ match {
//              case th: IOException =>
//                s"Input/Output error: ${th.getMessage}"
//              case InOutFailedWith(error) =>
//                s"You are $error!"
//            },
//          success =
//              name => s"Hello, $name!"
//        )
        .catchAll(
          ex =>
            ZIO.succeed(
              ex match {
                case th: IOException =>
                  th.getMessage
                case error: Exception =>
                  error.getMessage()
              }
            )
        )
        .tap((greeting: String) => Console.printLine(greeting))
        .repeatN(1)
        .exitCode


}
