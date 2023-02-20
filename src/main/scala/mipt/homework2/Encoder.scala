package mipt.homework2

import cats.Contravariant
import mipt.utils.Homeworks.TaskSyntax

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, Instant}

trait Encoder[T]:
  def apply(value: T): String

object Encoder:
  task"Реализуйте Contravariant для Encoder"
  given Contravariant[Encoder] = new Contravariant[Encoder]:
    override def contramap[A, B](fa: Encoder[A])(f: B => A): Encoder[B] = ???

object EncoderInstances:
  task"Реализуйте encoder числа в строку"
  given Encoder[Int] = ???

  task"Реализуйте encoder булева значения в строку"
  given Encoder[Boolean] = ???

  task"Реализуйте encoder для Option от произвольного типа, для которого есть Encoder в скоупе. Если None, то в результат записываем пустую строку"
  given[T: Encoder]: Encoder[Option[T]] = ???

  task"Реализуйте encoder для List и произвольного типа, для которого есть Encoder в скоупе. Элементы листа в результирующей строке должны быть разделены запятой."
  given[T](using Encoder[T]): Encoder[List[T]] = ???

  task"Реализуйте encoder для DayOfWeek через использование существующего encoder"
  given Encoder[DayOfWeek] = ???

  task"Реализуйте encoder для Instant через использование encoder, который НЕ реализован выше (его нужно добавить). Конечный формат Instant - 2023-02-17T17:00:00Z"
  given Encoder[Instant] = ???