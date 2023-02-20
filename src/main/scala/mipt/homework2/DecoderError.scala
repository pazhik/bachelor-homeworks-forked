package mipt.homework2

sealed trait DecoderError

case object NumberFormatDecoderError extends DecoderError

case object IllegalArgumentDecoderError extends DecoderError

case object DayOfWeekOutOfBoundError extends DecoderError

case object DateTimeParseError extends DecoderError
