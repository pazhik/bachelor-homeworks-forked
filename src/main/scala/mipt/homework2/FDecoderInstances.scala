package mipt.homework2

import mipt.utils.Homeworks.TaskSyntax
import mipt.homework2.domain.DegreesFahrenheit

import scala.util.{Failure, Success, Try}

trait OptionFDecoderInstances:
  import FDecoder._

  task"""Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе.
         Если исходная строка - пустая, или имеет значение `<none>` или null, то в результате должен быть None"""
  given [T](using fDecoder: FDecoder[T]): FDecoder[Option[T]] = ???

trait ListFDecoderInstances:
  import FDecoder._

  task"""Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе.
         Элементы листа в исходной строке разделены запятой."""
  given [T: FDecoder]: FDecoder[List[T]] = ???

object FDecoderInstances extends OptionFDecoderInstances, ListFDecoderInstances:
  import FDecoder.{_, given}
  import cats.implicits.toFunctorOps

  task"Реализуйте декодер из строки в строку"
  given strDecoder: FDecoder[String] = ???

    task"Реализуйте декодер из строки в число, используя `NumberFormatDecoderError` в результате в случае, если строка - не число"
  given intDecoder: FDecoder[Int] = ???

  task"Реализуйте декодер из строки в булево значение, используя ошибку `IllegalArgumentDecoderError` в случае, если строка не парсится в boolean"
  given boolDecoder: FDecoder[Boolean] = ???

  task"Реализуйте декодер для DegreesFahrenheit через использование существующего декодера, реализованного инстанса Functor и Either.left.map"
  given FDecoder[DegreesFahrenheit] = ???
