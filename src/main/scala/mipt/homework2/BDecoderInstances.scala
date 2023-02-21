package mipt.homework2

import mipt.utils.Homeworks.TaskSyntax
import mipt.homework2.*
import mipt.homework2.domain.DegreesFahrenheit

import scala.util.{Failure, Success, Try}

trait OptionBDecoderInstances:
  task"""Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе.
         Если исходная строка - пустая, или имеет значение `<none>` или null, то в результате должен быть None"""
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, Option[T]] = ???

trait ListBDecoderInstances:
  task"""Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе.
         Элементы листа в исходной строке разделены запятой."""
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, List[T]] = ???

object BDecoderInstances extends OptionBDecoderInstances, ListBDecoderInstances:
  import cats.implicits.toBifunctorOps
  import Decoder.given

  task"Реализуйте декодер из строки в строку"
  given strDecoder: Decoder[DecoderError, String] = ???

  task"Реализуйте декодер из строки в число с заданным типом ошибки, используя Decoder.attempt() и Bifunctor"
  given intDecoder: Decoder[NumberFormatDecoderError.type, Int] = ???

  task"Реализуйте декодер из строки в булево значение, используя Decoder.attempt() и Bifunctor"
  given Decoder[IllegalArgumentDecoderError.type, Boolean] = ???

  task"Реализуйте декодер для DegreesFahrenheit через использование существующего декодера и Bifunctor"
  given Decoder[InvalidDegreesFahrenheitValue.type, DegreesFahrenheit] = ???
