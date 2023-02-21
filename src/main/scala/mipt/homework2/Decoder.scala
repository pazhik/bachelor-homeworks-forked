package mipt.homework2

import cats.{Bifunctor, Functor}
import mipt.homework2.Decoder.Result
import mipt.utils.Homeworks.TaskSyntax

import java.time.{DayOfWeek, Instant}
import scala.util.Try

trait Decoder[+E, +T]:
  def apply(raw: String): Decoder.Result[E, T]

object Decoder:

  type Result[E, T] = Either[E, T]

  def apply[E, T](using decoder: Decoder[E, T]): Decoder[E, T] = decoder

  def attempt[T](unsafe: String => T): Decoder[Throwable, T] =
    (raw: String) => Try(unsafe(raw)).toEither

  def decode[E, T](raw: String)(using decoder: Decoder[E, T]): Decoder.Result[E, T] =
    decoder(raw)

  task"Реализуйте Bifunctor для Decoder, используя Either.left проекцию"
  given Bifunctor[Decoder] = new Bifunctor[Decoder]:
    override def bimap[A, B, C, D](fab: Decoder[A, B])(f: A => C, g: B => D): Decoder[C, D] = ???

object FDecoder:

  type FDecoder[T] = Decoder[DecoderError, T]

  task"Реализуйте Functor для Decoder"
  given Functor[FDecoder] = new Functor[FDecoder]:
    override def map[A, B](fa: FDecoder[A])(f: A => B): FDecoder[B] = ???

// не смог до конца сформировать свои мысли, но была идея подумать над вариантом сделать парсеры древовидной структуры, чтобы их можно было описывать в виде dsl:
//
//final case class OptionGroupSimple(i: Option[Int], b: Option[Boolean], s: Option[String])
//final case class OptionGroupComplex(foo: Option[Int], bar: Option[String], anotherGroup: OptionGroupSimple)
//
//given ConfigParser[OptionGroupComplex] = (
//  OptionParser[Int]("foo"),
//  OptionParser[String]("bar"),
//  OptionGroupParser("anotherGroup") {
//    (
//      OptionParser[Int]("intOption"),
//      OptionParser[Boolean]("boolOption"),
//      OptionParser[String]("stringOption")
//    ).mapN(OptionGroupSimple)
//  }
//).mapN(OptionGroupComplex)

//sealed trait Node
//
//final case class Option[T](value: T) extends Node
//
//final case class OptionGroup(options: Map[String, Node]) extends Node
