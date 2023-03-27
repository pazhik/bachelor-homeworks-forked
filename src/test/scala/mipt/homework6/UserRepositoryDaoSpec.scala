package mipt.homework6

import mipt.homework6.User._
import mipt.homework6.UserErrors.{UserAlreadyExists, UserDoesNotExists}
import mipt.homework6.UserRepository.Config
import zio._
import zio.test._
import zio.test.Assertion._
import zio.test.TestEnvironment

import scala.util.Random

object UserRepositoryDaoSpecData:
  val sampleUsers: List[User] = List(
    User(UserId(0), UserName("Subject #0"), Age(42), Set.empty),
    User(UserId(1), UserName("Subject #1"), Age(41), Set.empty),
    User(UserId(2), UserName("Subject #2"), Age(40), Set.empty),
    User(UserId(3), UserName("Subject #3"), Age(39), Set.empty),
    User(UserId(4), UserName("Subject #4"), Age(38), Set.empty)
  )

  val testConfig = Config(10)

  def dao(users: List[User]) = new UserRepositoryDao:
    var usersList = users.toSet

    override def findAll(config: Config): List[User] =
      usersList.toList

    override def create(name: UserName, age: Age, friends: Set[UserId])(
        config: Config
    ): Either[UserAlreadyExists, User] =
      if (usersList.exists(_.name == name)) Left(UserAlreadyExists(name))
      else
        val user = User(UserId(usersList.size), name, age, friends)
        usersList += user
        Right(user)

    override def delete(userId: UserId)(config: Config): Either[UserDoesNotExists, Unit] =
      if (usersList.exists(_.id == userId))
        usersList -= usersList.find(_.id == userId).get
        Right(())
      else Left(UserDoesNotExists(userId))

    override def update(user: User)(config: Config): Either[UserDoesNotExists, Unit] =
      if (usersList.exists(_.id == user.id))
        usersList -= usersList.find(_.id == user.id).get
        usersList += user
        Right(())
      else Left(UserDoesNotExists(user.id))


object UserRepositorySpec extends ZIOSpecDefault {
  import UserRepositoryDaoSpecData._

  def spec = suite("UserRepositorySpec")(
    test("findAll should return all users") {
      for {
        dao           <- ZIO.succeed(dao(sampleUsers))
        userRepository = UserRepository(dao)
        users         <- userRepository.findAll
      } yield assert(users.toSet)(equalTo(sampleUsers.toSet))
    },
    test("create should add a new user") {
      for {
        dao           <- ZIO.succeed(dao(sampleUsers))
        userRepository = UserRepository(dao)
        newUser       <- userRepository.create(UserName("Subject #5"), Age(37))
        users         <- userRepository.findAll
      } yield assert(users.toSet)(equalTo((newUser :: sampleUsers).toSet))
    },
    test("create should fail if user already exists") {
      for {
        dao           <- ZIO.succeed(dao(sampleUsers))
        userRepository = UserRepository(dao)
        result        <- userRepository.create(UserName("Subject #1"), Age(41)).either
      } yield assert(result)(isLeft(equalTo(UserAlreadyExists(UserName("Subject #1")))))
    },
    test("delete should remove the specified user") {
      for {
        dao           <- ZIO.succeed(dao(sampleUsers))
        userRepository = UserRepository(dao)
        _             <- userRepository.delete(UserId(2))
        users         <- userRepository.findAll
      } yield assert(users.toSet)(equalTo(sampleUsers.filterNot(_.id == UserId(2)).toSet))
    },
    test("delete should fail if user does not exist") {
      for {
        dao           <- ZIO.succeed(dao(sampleUsers))
        userRepository = UserRepository(dao)
        result        <- userRepository.delete(UserId(10)).either
      } yield assert(result)(isLeft(equalTo(UserDoesNotExists(UserId(10)))))
    },
    test("update should update the specified user") {
      for {
        dao           <- ZIO.succeed(dao(sampleUsers))
        userRepository = UserRepository(dao)
        updatedUser    = User(UserId(2), UserName("Subject #2"), Age(30), Set.empty)
        _             <- userRepository.update(updatedUser)
        users         <- userRepository.findAll
      } yield assert(users.find(_.id == UserId(2)).get)(equalTo(updatedUser))
    },
    test("update should fail if user does not exist") {
      for {
        dao           <- ZIO.succeed(dao(sampleUsers))
        userRepository = UserRepository(dao)
        updatedUser    = User(UserId(10), UserName("Subject #10"), Age(20), Set.empty)
        result        <- userRepository.update(updatedUser).either
      } yield assert(result)(isLeft(equalTo(UserDoesNotExists(UserId(10)))))
    }
  ).provideLayer(ZLayer.succeed(testConfig))
}
