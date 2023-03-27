package mipt.homework6


object User {
    type UserId     = Int
    type UserName   = String
    type UserAge    = Byte
    val AdultAge    = 18
}
import User._

final case class User(
    id:     UserId,
    name:   UserName,
    age:    UserAge,
    friends: Set[UserId]
) {
    def isAdult: Boolean = age >= AdultAge
}
