package mipt.homework3

import cats.MonadThrow
import cats.data.OptionT
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import cats.syntax.traverse.toTraverseOps

import scala.concurrent.Future
import scala.util.control.NoStackTrace

trait UserRepository[F[_]]:
  def findAll(): F[List[User]]
  def create(name: UserName, age: Age, friends: Set[UserId] = Set.empty): F[User]
  def delete(userId: UserId): F[Unit]
  def update(user: User): F[Unit]

object UserRepository:
  final case class UserNotFoundError(id: UserId) extends Throwable

  type Operation[F[_], T] = UserRepository[F] => F[T]
  object Operation:
//    given [F[_]](using m: Monad[F]): Monad[Operation[F, *]] = new Monad[Operation[F, *]]:
//      override def flatMap[A, B](fa: Operation[F, A])(f: A => Operation[F, B]): Operation[F, B] = ???
//
//      override def tailRecM[A, B](a: A)(f: A => Operation[F, Either[A, B]]): Operation[F, B] = ???
//
//      override def pure[A](x: A): Operation[F, A] = ???

//    given repoOperationME[F[_], E](using me: MonadError[F, E]): MonadError[Operation[F, *], E] =
//      new MonadError[Operation[F, *], E]:
//        override def flatMap[A, B](fa: Operation[F, A])(f: A => Operation[F, B]): Operation[F, B] = ???
//
//        override def tailRecM[A, B](a: A)(f: A => Operation[F, Either[A, B]]): Operation[F, B] = ???
//
//        override def pure[A](x: A): Operation[F, A] = ???
//
//        override def raiseError[A](e: E): Operation[F, A] = _ => me.raiseError(e)
//
//        override def handleErrorWith[A](fa: Operation[F, A])(f: E => Operation[F, A]): Operation[F, A] =
//          repo => me.handleErrorWith(fa(repo))(e => f(e)(repo))

    given repoOperationME[F[_]](using me: MonadThrow[F]): MonadThrow[Operation[F, *]] =
      new MonadThrow[Operation[F, *]]:
        override def flatMap[A, B](fa: Operation[F, A])(f: A => Operation[F, B]): Operation[F, B] =
          ???

        override def tailRecM[A, B](a: A)(f: A => Operation[F, Either[A, B]]): Operation[F, B] =
          ???

        override def pure[A](x: A): Operation[F, A] =
          ???

        override def raiseError[A](e: Throwable): Operation[F, A] =
          ???

        override def handleErrorWith[A](fa: Operation[F, A])(f: Throwable => Operation[F, A]): Operation[F, A] =
          ???

  object Operations:
    import UserRepository.Operation.given

    def findAll[F[_]](): UserRepository.Operation[F, List[User]] =
      _.findAll()

    def create[F[_]](name: UserName, age: Age, friends: Set[UserId] = Set.empty): UserRepository.Operation[F, User] =
      _.create(name, age, friends)

    def delete[F[_]](userId: UserId): UserRepository.Operation[F, Unit] = _.delete(userId)

    def update[F[_]](user: User): UserRepository.Operation[F, Unit] = _.update(user)

    // реализуйте композитные методы, используя базовые выше

    /** Метод опционального поиска пользователя */
    def findMaybe[F[_]](userId: UserId)(using me: MonadThrow[F]): UserRepository.Operation[F, Option[User]] =
      ???

    /** Метод поиска пользователя. Если пользователь не найден, должна генерироваться ошибка UserNotFound */
    def find[F[_]](userId: UserId)(using me: MonadThrow[F]): UserRepository.Operation[F, User] =
      ???

    /** Метод добавления друга к пользователю. */
    def addFriend[F[_]](currentUserId: UserId, friendId: UserId)(using
        me: MonadThrow[F]
    ): UserRepository.Operation[F, User] =
      ???

    /** Метод удаления друга от пользователю */
    def deleteFriend[F[_]](currentUserId: UserId, friendId: UserId)(using
        me: MonadThrow[F]
    ): UserRepository.Operation[F, User] =
      ???

    /** Метод получения всех друзей пользователя */
    def getUserFriends[F[_]](userId: UserId)(using
        me: MonadThrow[F]
    ): UserRepository.Operation[F, List[User]] =
      ???

    /** Метод получения пользователей, у которых в друзьях только взрослые пользователи */
    def getUsersWithAdultOnlyFriends[F[_]]()(using
        me: MonadThrow[F]
    ): UserRepository.Operation[F, List[User]] =
      ???

    /** Метод удаления всех молодых пользователей
      */
    def deleteAllJuniorUsers[F[_]]()(using
        me: MonadThrow[F]
    ): UserRepository.Operation[F, Unit] =
      ???

    /** Метод создания сообщества, где все являются друзьями друг для друга. На вход подается список атрибутов
      * пользователей из сообщества
      */
    def createCommunity[F[_]](community: List[(UserName, Age)])(using
        me: MonadThrow[F]
    ): UserRepository.Operation[F, List[User]] =
      ???
