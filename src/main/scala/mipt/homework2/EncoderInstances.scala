package mipt.homework2

import mipt.homework2.domain.DegreesFahrenheit

trait OptionEncoderInstances:
  given [T](using e: Encoder[T]): Encoder[Option[T]] =
    task"""Реализуйте Encoder для Option и произвольного типа, для которого есть Encoder в скоупе.
           None должен преобразовываться в значение `<none>`""" (3, 1)

trait ListEncoderInstances:
  given [T: Encoder]: Encoder[List[T]] =
    task"""Реализуйте Encoder для List и произвольного типа, для которого есть Encoder в скоупе.
           Элементы листа в результирующей строке должны быть разделены запятой.""" (3, 2)

object EncoderInstances extends OptionEncoderInstances, ListEncoderInstances:
  import Encoder.given_Contravariant_Encoder
  import cats.implicits.toContravariantOps

  given Encoder[String] =
    task"Реализуйте encoder для строки" (3, 3)

  given Encoder[Int] =
    task"Реализуйте encoder числа в строку" (3, 4)

  given Encoder[Boolean] =
    task"Реализуйте encoder булева значения в строку" (3, 5)

  task"Попробуйте обобщить вышеописанные Encoder-ы одним инстансом"

  given Encoder[DegreesFahrenheit] =
    task"Реализуйте encoder для DegreesFahrenheit через использование существующего encoder и Contravariant" (3, 6)
