package mipt.homework2

import cats.{Bifunctor, Functor}
import mipt.homework2.Decoder.Result

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

  given Bifunctor[Decoder] =
    task"Реализуйте Bifunctor для Decoder, используя Either.left проекцию" (2, 0)

object FDecoder:

  type FDecoder[T] = Decoder[DecoderError, T]

  def decode[T](raw: String)(using decoder: FDecoder[T]): Decoder.Result[DecoderError, T] =
    decoder(raw)

  given Functor[FDecoder] = task"Реализуйте Functor для Decoder" (1, 0)
