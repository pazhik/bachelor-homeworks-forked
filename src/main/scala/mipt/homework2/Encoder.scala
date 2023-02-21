package mipt.homework2

import cats.Contravariant
import mipt.utils.Homeworks.TaskSyntax

trait Encoder[-T]:
  def apply(value: T): String

object Encoder:
  def encode[T](value: T)
               (using encoder: Encoder[T]): String =
    encoder(value)

  task"Реализуйте Contravariant для Encoder"
  given Contravariant[Encoder] = new Contravariant[Encoder]:
    override def contramap[A, B](fa: Encoder[A])(f: B => A): Encoder[B] = ???
