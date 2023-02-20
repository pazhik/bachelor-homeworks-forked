package homework2

import mipt.homework2.Encoder
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.{DayOfWeek, Instant}

class EncoderTest extends AnyFlatSpec with Matchers with Inside:

  import mipt.homework2.EncoderInstances.given

  behavior.of("Encoder")

  it should "correctly encode int" in {
    Encoder.encode(123) shouldBe "123"
  }

  it should "correctly encode boolean" in {
    Encoder.encode(true) shouldBe "true"
  }

  it should "correctly encode day of week" in {
    Encoder.encode(DayOfWeek.MONDAY) shouldBe "1"
  }

  it should "correctly encode instant" in {
    Encoder.encode(Instant.ofEpochSecond(1676653200)) shouldBe "2023-02-17T17:00:00Z"
  }

  it should "correctly encode option" in {
//    Encoder.encode(Some(123)) shouldBe "123"
//    Encoder.encode(Some("abc")) shouldBe "abc"
//    Encoder.encode(None) shouldBe ""
  }

  it should "correctly encode list" in {
    Encoder.encode(List(123, 321, 333)) shouldBe "123,321,333"
    Encoder.encode(List.empty[Int]) shouldBe ""
//    Encoder.encode(List(None)) shouldBe ""
//    Encoder.encode(List(Some(123), Some(321), None)) shouldBe "123,321"
  }