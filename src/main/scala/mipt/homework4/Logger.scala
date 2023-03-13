package mipt.homework4

import cats.Monad
import cats.data.WriterT
import cats.implicits.catsSyntaxApplicativeId
import mipt.utils.Homeworks.TaskSyntax

case class Debug(debug: String)
case class Info(info: String)
case class Error(error: String)

type WriterTF[F[_], L] = [A] =>> WriterT[F, L, A]
case class LogEmbed[F[_], A](value: WriterT[WriterTF[WriterTF[F, Vector[Debug]], Vector[Info]], Vector[Error], A])

final case class Logger[F[_]: Monad]():
  task"""
        Реализуйте функции debug, info и error, осуществляющие логгирование в соответствующий канал WriterT
        """ (1, 1)
  def debug(debug: String): LogEmbed[F, Unit] = ???
  def info(info: String): LogEmbed[F, Unit] = ???
  def error(error: String): LogEmbed[F, Unit] = ???
