package mipt.homework2

import org.scalatest.Ignore
import org.scalatest.flatspec.{AnyFlatSpec, AnyFlatSpecLike}
import org.scalatest.matchers.should.Matchers

import java.time.{DayOfWeek, Instant}

class CommonDecoderTests(using
    i: Decoder[DecoderError, Int],
    b: Decoder[DecoderError, Boolean],
    d: Decoder[DecoderError, DayOfWeek],
    inst: Decoder[DecoderError, Instant],
    o: Decoder[DecoderError, Option[Int]],
    li: Decoder[DecoderError, List[Int]],
    loi: Decoder[DecoderError, List[Option[Int]]],
    oli: Decoder[DecoderError, Option[List[Int]]]
) extends AnyFlatSpec with Matchers:
  behavior.of("Decoder")

  it should "correctly decode int" in {
    Decoder.decode[DecoderError, Int]("123") shouldBe Right(123)
    Decoder.decode[DecoderError, Int]("aaa1") shouldBe Left(NumberFormatDecoderError)
  }

  it should "correctly decode boolean" in {
    Decoder.decode[DecoderError, Boolean]("true") shouldBe Right(true)
    Decoder.decode[DecoderError, Boolean]("aaa1") shouldBe Left(IllegalArgumentDecoderError)
  }

  it should "correctly decode day of week" in {
    Decoder.decode[DecoderError, DayOfWeek]("1") shouldBe Right(DayOfWeek.MONDAY)
    Decoder.decode[DecoderError, DayOfWeek]("10") shouldBe Left(DayOfWeekOutOfBoundError)
  }

  it should "correctly decode instant" in {
    Decoder.decode[DecoderError, Instant]("2023-02-17T17:00:00Z") shouldBe Right(Instant.ofEpochSecond(1676653200))
    Decoder.decode[DecoderError, Instant]("not-parsable-instant") shouldBe Left(DateTimeParseError)
  }

  it should "correctly decode option" in {
    Decoder.decode[DecoderError, Option[Int]]("123") shouldBe Right(Some(123))
    Decoder.decode[DecoderError, Option[Int]]("abc") shouldBe Left(NumberFormatDecoderError)
    Decoder.decode[DecoderError, Option[Int]]("") shouldBe Right(None)
  }

  it should "correctly decode list" in {
    Decoder.decode[DecoderError, List[Int]]("123, 321, 333") shouldBe Right(List(123, 321, 333))
    Decoder.decode[DecoderError, List[Int]]("abc") shouldBe Left(NumberFormatDecoderError)
    Decoder.decode[DecoderError, List[Int]]("") shouldBe Right(List.empty)
    Decoder.decode[DecoderError, List[Option[Int]]]("1, 2, 3") shouldBe Right(List(Some(1), Some(2), Some(3)))
    Decoder.decode[DecoderError, Option[List[Int]]]("1, 2, 3") shouldBe Right(Some(List(1, 2, 3)))
  }
