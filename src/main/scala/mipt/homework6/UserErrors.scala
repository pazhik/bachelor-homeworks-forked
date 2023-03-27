package mipt.homework6

import User._

object UserErrors {
    case class UserAlreadyExists(name: UserName)
    case class UserDoesNotExists(id: UserId)
}
