package mipt.homework2

import cats.Functor
import mipt.homework2.Decoder.Result
import mipt.utils.Homeworks.TaskSyntax

import java.time.{DayOfWeek, Instant}

trait Decoder[T]:
  def apply(raw: String): Decoder.Result[T]


object Decoder:

  trait Error

  type Result[T] = Either[Error, T]

  def apply[T](using decoder: Decoder[T]) = decoder

  def decode[T](raw: String)
               (using decoder: Decoder[T]): Decoder.Result[T] =
    decoder(raw)

  task"Реализуйте Functor для Decoder"
  given Functor[Decoder] = new Functor[Decoder]:
    override def map[A, B](fa: Decoder[A])(f: A => B): Decoder[B] = ???

trait OptionDecoderInstances:
  task"Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе. Если исходная строка пустая или null, в результате должен быть None"
  given[T](using Decoder[T]): Decoder[Option[T]] = ???

trait ListDecoderInstances:
  task"Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе. Элементы листа в исходной строке по условию задачи разделены запятой."
  given[T: Decoder]: Decoder[List[T]] = ???


object DecoderInstances extends OptionDecoderInstances, ListDecoderInstances:
  case object NumberFormatDecoderError extends Decoder.Error

  case object IllegalArgumentDecoderError extends Decoder.Error

  case object DayOfWeekOutOfBoundError extends Decoder.Error
  
  case object DateTimeParseError extends Decoder.Error

  task"Реализуйте декодер из строки в число, используя `NumberFormatDecoderError` в результате в случае, если строка - не число"
  given Decoder[Int] = ???

  task"Реализуйте декодер из строки в булево значение, используя `IllegalArgumentDecoderError` в результате в случае, если строка не парсится в boolean"
  given Decoder[Boolean] = ???

  task"Реализуйте декодер для DayOfWeek через использование существующего декодера"
  given Decoder[DayOfWeek] = ???

  task"Реализуйте декодер для Instant через использование декодера, который НЕ реализован выше (его нужно добавить). Instant в строке в формате 2023-02-17T17:00:00Z"
  given Decoder[Instant] = ???


// не смог до конца сформировать свои мысли, но была идея подумать над вариантом сделать парсеры древовидной структуры, чтобы их можно было описывать в виде dsl:
//
//final case class OptionGroupSimple(i: Option[Int], b: Option[Boolean], s: Option[String])
//final case class OptionGroupComplex(foo: Option[Int], bar: Option[String], anotherGroup: OptionGroupSimple)
//
//given ConfigParser[OptionGroupComplex] = (
//  OptionParser[Int]("foo"),
//  OptionParser[String]("bar"),
//  OptionGroupParser("anotherGroup") {
//    (
//      OptionParser[Int]("intOption"),
//      OptionParser[Boolean]("boolOption"),
//      OptionParser[String]("stringOption")
//    ).mapN(OptionGroupSimple)
//  }
//).mapN(OptionGroupComplex)

sealed trait Node

final case class Option[T](value: T) extends Node

final case class OptionGroup(options: Map[String, Node]) extends Node
