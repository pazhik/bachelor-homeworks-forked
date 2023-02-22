package mipt.homework2

import mipt.homework2.domain.DegreesFahrenheit
import Decoder.*
import cats.implicits.toBifunctorOps
import scala.util.{Failure, Success, Try}

trait OptionBDecoderInstances:
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, Option[T]] =
    task"""Реализуйте декодер для Option и произвольного типа,
           для которого есть Decoder в скоупе. Если исходная строка - пустая,
           или имеет значение `<none>` или null, то в результате должен быть None""" (2, 1)

trait ListBDecoderInstances:
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, List[T]] =
    task"""Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе.
           Элементы листа в исходной строке разделены запятой.""" (2, 2)

object BDecoderInstances extends OptionBDecoderInstances, ListBDecoderInstances:
  given strDecoder: Decoder[DecoderError, String] =
    task"Реализуйте декодер из строки в строку" (2, 3)

  given intDecoder: Decoder[NumberFormatDecoderError.type, Int] =
    task"Реализуйте декодер из строки в число с заданным типом ошибки, используя Decoder.attempt() и Bifunctor" (2, 4)

  given Decoder[IllegalArgumentDecoderError.type, Boolean] =
    task"Реализуйте декодер из строки в булево значение, используя Decoder.attempt() и Bifunctor" (2, 5)

  given Decoder[InvalidDegreesFahrenheitValue.type, DegreesFahrenheit] =
    task"Реализуйте декодер для DegreesFahrenheit через использование существующего декодера и Bifunctor" (2, 6)
