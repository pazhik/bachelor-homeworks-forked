package mipt.homework2

import mipt.homework2.domain.DegreesFahrenheit
import mipt.utils.Homeworks.TaskSyntax


trait OptionEncoderInstances:
  task"""Реализуйте Encoder для Option и произвольного типа, для которого есть Encoder в скоупе.
         None должен преобразовываться в значение `<none>`"""
  given[T](using e: Encoder[T]): Encoder[Option[T]] = ???

trait ListEncoderInstances:
  task"""Реализуйте Encoder для List и произвольного типа, для которого есть Encoder в скоупе.
         Элементы листа в результирующей строке должны быть разделены запятой."""
  given[T: Encoder]: Encoder[List[T]] = ???

object EncoderInstances extends OptionEncoderInstances, ListEncoderInstances:
  import Encoder.given_Contravariant_Encoder
  import cats.implicits.toContravariantOps

  task"Реализуйте encoder для строки"
  given strEncoder: Encoder[String] = ???

  task"Реализуйте encoder числа в строку"
  given intEncoder: Encoder[Int] = ???

  task"Реализуйте encoder булева значения в строку"
  given booleanEncoder: Encoder[Boolean] = ???
  
  task"Попробуйте обобщить вышеописанные Encoder-ы одним инстансом"

  task"Реализуйте encoder для DegreesFahrenheit через использование существующего encoder и Contravariant"
  given Encoder[DegreesFahrenheit] = ???
