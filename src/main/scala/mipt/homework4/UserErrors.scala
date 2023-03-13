package mipt.homework4

object UserErrors:
  case class UserAlreadyExists(name: UserName)
  case class UserDoesNotExists(id: UserId)
