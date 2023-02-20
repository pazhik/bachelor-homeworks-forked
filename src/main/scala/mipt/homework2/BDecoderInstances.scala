package mipt.homework2

import mipt.utils.Homeworks.TaskSyntax
import mipt.homework2._

import java.time.{DayOfWeek, Instant}

trait OptionBDecoderInstances:
  task"Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе. Если исходная строка пустая или null, в результате должен быть None"
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, Option[T]] = ???

trait ListBDecoderInstances:
  task"Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе. Элементы листа в исходной строке по условию задачи разделены запятой."
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, List[T]] = ???

object BDecoderInstances extends OptionBDecoderInstances, ListBDecoderInstances:
  import cats.implicits.toBifunctorOps
  import Decoder.given

  task"Реализуйте декодер из строки в число с заданным типом ошибки, используя Decoder.attempt() и Bifunctor"
  given Decoder[NumberFormatDecoderError, Int] = ???

  task"Реализуйте декодер из строки в булево значение, используя Decoder.attempt() и Bifunctor"
  given Decoder[IllegalArgumentDecoderError, Boolean] = ???

  task"Реализуйте декодер для DayOfWeek через использование существующего декодера и реализованного инстанса Functor"
  given Decoder[DayOfWeekOutOfBoundError, DayOfWeek] = ???

  task"Реализуйте декодер для Instant через использование декодера, который НЕ реализован выше (его нужно добавить), и реализованного инстанса Functor. Instant в строке в формате 2023-02-17T17:00:00Z"
  given Decoder[DateTimeParseError, Instant] = ???
