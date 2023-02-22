package mipt.homework2

import mipt.homework2.domain.DegreesFahrenheit
import scala.util.{Failure, Success, Try}
import cats.implicits.toFunctorOps
import FDecoder.*

trait OptionFDecoderInstances:
  given [T](using FDecoder[T]): FDecoder[Option[T]] =
    task"""Реализуйте декодер для Option и произвольного типа,
           для которого есть Decoder в скоупе.
           Если исходная строка - пустая, или имеет значение `<none>` или null,
           то в результате должен быть None""" (1, 1)

trait ListFDecoderInstances:
  given [T: FDecoder]: FDecoder[List[T]] =
    task"""Реализуйте декодер для List и произвольного типа,
           для которого есть Decoder в скоупе.
           Элементы листа в исходной строке разделены запятой.""" ()

object FDecoderInstances extends OptionFDecoderInstances, ListFDecoderInstances:
  given strDecoder: FDecoder[String] =
    task"Реализуйте декодер из строки в строку" (1, 2)

  given intDecoder: FDecoder[Int] =
    task"""Реализуйте декодер из строки в число,
           используя `NumberFormatDecoderError`в результате в случае,
           если строка - не число""" (1, 3)

  given boolDecoder: FDecoder[Boolean] =
    task"""Реализуйте декодер из строки в булево значение,
           используя ошибку `IllegalArgumentDecoderError` в случае,
           если строка не парсится в boolean""" (1, 4)

  given FDecoder[DegreesFahrenheit] =
    task"""Реализуйте декодер для DegreesFahrenheit
           через использование существующего декодера,
           реализованного инстанса Functor и Either.left.map""" (1, 5)
