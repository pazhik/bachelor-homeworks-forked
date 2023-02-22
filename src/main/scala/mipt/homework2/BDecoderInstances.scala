package mipt.homework2

import mipt.utils.Homeworks.TaskSyntax
import mipt.homework2.*
import mipt.homework2.Decoder.{Result, decode}

import java.time.{DayOfWeek, Instant}

trait OptionBDecoderInstances
//  task"Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе. Если исходная строка пустая или null, в результате должен быть None"
//  given [E, T](using decoder: Decoder[E, T]): Decoder[E, Option[T]] = new Decoder[E, Option[E]] {
//    def apply(raw: String): Decoder.Result[E, T] =
//      raw match {
//        case "" => new Decoder[E, Option[T]] {
//          def apply(raw: String): Result[E, Option[T]] = Right(Option.empty)
//        }
//        case x => new Decoder[E, Option[T]] {
//          def apply(raw: String): Result[E, Option[T]] = decoder(x).map(Option(_))
//        }
//      }
//  }


trait ListBDecoderInstances
//  task"Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе. Элементы листа в исходной строке по условию задачи разделены запятой."
//  given [E, T](using decoder: Decoder[E, T]): Decoder[E, List[T]] = ???

object BDecoderInstances extends OptionBDecoderInstances, ListBDecoderInstances:
  import cats.implicits.toBifunctorOps
//  import Decoder.given

//  task"Реализуйте декодер из строки в число с заданным типом ошибки, используя Decoder.attempt() и Bifunctor"
//  given Decoder[NumberFormatDecoderError, Int] = ???
//
//  task"Реализуйте декодер из строки в булево значение, используя Decoder.attempt() и Bifunctor"
//  given Decoder[IllegalArgumentDecoderError, Boolean] = ???
//
//  task"Реализуйте декодер для DayOfWeek через использование существующего декодера и реализованного инстанса Functor"
//  given Decoder[DayOfWeekOutOfBoundError, DayOfWeek] = ???
//
//  task"Реализуйте декодер для Instant через использование декодера, который НЕ реализован выше (его нужно добавить), и реализованного инстанса Functor. Instant в строке в формате 2023-02-17T17:00:00Z"
//  given Decoder[DateTimeParseError, Instant] = ???


//import BDecoderInstances.{given_Decoder_E_Option, given_Decoder_E_List}



//case class Foo[F[_], A](f: F[A] => A)