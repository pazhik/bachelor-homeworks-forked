package mipt.homework3
import math.Ordering.Implicits.infixOrderingOps

opaque type UserId = Int
opaque type UserName = String
opaque type Age = Byte

object Age:
  val Adult: Age = 18
  given Ordering[Age] = scala.math.Ordering.Byte

final case class User(id: UserId, name: UserName, age: Age, friends: Set[UserId]):
  def isAdult: Boolean = age >= Age.Adult
