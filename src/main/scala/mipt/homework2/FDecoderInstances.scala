package mipt.homework2

import mipt.utils.Homeworks.TaskSyntax

import java.time.{DayOfWeek, Instant}

trait OptionFDecoderInstances:
  import FDecoder._

  task"Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе. Если исходная строка пустая или null, в результате должен быть None"
  given [T](using fDecoder: FDecoder[T]): FDecoder[Option[T]] = ???

trait ListFDecoderInstances:
  import FDecoder._

  task"Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе. Элементы листа в исходной строке по условию задачи разделены запятой."
  given [T: FDecoder]: FDecoder[List[T]] = ???

object FDecoderInstances extends OptionFDecoderInstances, ListFDecoderInstances:
  import FDecoder._
  import cats.implicits.toFunctorOps

  task"Реализуйте декодер из строки в число, используя `NumberFormatDecoderError` в результате в случае, если строка - не число"
  given FDecoder[Int] = ???

  task"Реализуйте декодер из строки в булево значение, используя `IllegalArgumentDecoderError` в результате в случае, если строка не парсится в boolean"
  given FDecoder[Boolean] = ???

  task"Реализуйте декодер для DayOfWeek через использование существующего декодера и реализованного инстанса Functor"
  given FDecoder[DayOfWeek] = ???

  task"Реализуйте декодер для Instant через использование декодера, который НЕ реализован выше (его нужно добавить), и реализованного инстанса Functor. Instant в строке в формате 2023-02-17T17:00:00Z"
  given FDecoder[Instant] = ???
