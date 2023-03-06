package mipt.homework3

import cats.data.OptionT
import cats.syntax.monad.given
import cats.{Monad, MonadError}
import mipt.homework3.UserRepository.{RepoError, UserNotFoundError}

import scala.concurrent.Future

abstract class UserRepository[F[_]: Monad]:
  def findAll(): F[List[User]]
  def create(name: UserName, age: Age, friends: Set[UserId] = Set.empty): F[User]
  def delete(user: User): F[Unit]
  def update(user: User): F[Unit]

object UserRepository:
  sealed trait RepoError
  final case class UserNotFoundError(id: UserId) extends RepoError

  type Operation[F[_], T] = UserRepository[F] => F[T]
  object Operation:
//    given [F[_]](using m: Monad[F]): Monad[Operation[F, *]] = new Monad[Operation[F, *]]:
//      override def flatMap[A, B](fa: Operation[F, A])(f: A => Operation[F, B]): Operation[F, B] = ???
//
//      override def tailRecM[A, B](a: A)(f: A => Operation[F, Either[A, B]]): Operation[F, B] = ???
//
//      override def pure[A](x: A): Operation[F, A] = ???

    given [F[_], E](using me: MonadError[F, E]): MonadError[Operation[F, *], E] =
      new MonadError[Operation[F, *], E]:
        override def flatMap[A, B](fa: Operation[F, A])(f: A => Operation[F, B]): Operation[F, B] = ???

        override def tailRecM[A, B](a: A)(f: A => Operation[F, Either[A, B]]): Operation[F, B] = ???

        override def pure[A](x: A): Operation[F, A] = ???

        override def raiseError[A](e: E): Operation[F, A] = _ => me.raiseError(e)

        override def handleErrorWith[A](fa: Operation[F, A])(f: E => Operation[F, A]): Operation[F, A] =
          repo => me.handleErrorWith(fa(repo))(e => f(e)(repo))

object Users:
  import UserRepository.Operation.given
  import cats.syntax.flatMap.toFlatMapOps
  import cats.syntax.functor.toFunctorOps

  def findAll[F[_]](): UserRepository.Operation[F, List[User]] =
    _.findAll()

  def create[F[_]](name: UserName, age: Age, friends: Set[UserId]): UserRepository.Operation[F, User] =
    _.create(name, age, friends)

  def delete[F[_]](user: User): UserRepository.Operation[F, Unit] = _.delete(user)

  def update[F[_]](user: User): UserRepository.Operation[F, Unit] = _.update(user)

  // реализуйте композитные методы, используя базовые выше

  def findMaybe[F[_]](user: User): UserRepository.Operation[F, Option[User]] = ???

  /** Метод поиска пользователя. Если пользователь не найден, должна генерироваться ошибка UserNotFound */
  def find[F[_]](user: User)(using me: MonadError[F, RepoError]): UserRepository.Operation[F, User] = ???

  /** Метод добавления друга к пользователю */
  def addFriend[F[_]](currentUser: User, friend: User)(using
      me: MonadError[F, RepoError]
  ): UserRepository.Operation[F, User] = ???

  /** Метод удаления друга от пользователю */
  def deleteFriend[F[_]: Monad](currentUser: User, friend: User)(using
      me: MonadError[F, RepoError]
  ): UserRepository.Operation[F, Unit] = ???

  /** Метод получения всех друзей пользователя */
  def getUserFriends[F[_]: Monad](user: User)(using
      me: MonadError[F, RepoError]
  ): UserRepository.Operation[F, List[User]] = ???

  /** Метод получения имен пользователей, у которых в друзьях только взрослые пользователи */
  def getUserNamesWithOnlyAdultFriends[F[_]: Monad]()(using
      me: MonadError[F, RepoError]
  ): UserRepository.Operation[F, List[UserName]] = ???

  /** Метод удаления всех молодых пользователей
    */
  def deleteAllNonAdultUsers[F[_]: Monad]()(using
      me: MonadError[F, RepoError]
  ): UserRepository.Operation[F, Unit] = ???

  /** Метод создания сообщества, где все являются друзьями друг для друга. На вход подается список атрибутов
    * пользователей из сообщества
    */
  def createCommunity[F[_]: Monad](community: List[(UserName, Age)])(using
      me: MonadError[F, RepoError]
  ): UserRepository.Operation[F, List[User]] = ???
