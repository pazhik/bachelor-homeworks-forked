package mipt.homework4

import cats.MonadThrow
import cats.mtl.Ask
import mipt.homework4.UserErrors.*

case class Config(chunkSize: Int)

trait UserRepositoryDao:
  def findAll(config: Config): List[User]
  def create(name: UserName, age: Age, friends: Set[UserId] = Set.empty)(config: Config): Either[UserAlreadyExists, User]
  def delete(userId: UserId)(config: Config): Either[UserDoesNotExists, Unit]
  def update(user: User)(config: Config): Either[UserDoesNotExists, Unit]

trait UserRepository[F[_]]:
  def findAll: F[List[User]]
  def create(name: UserName, age: Age, friends: Set[UserId] = Set.empty): F[User]
  def delete(userId: UserId): F[Unit]
  def update(user: User): F[Unit]

object UserRepositoryDao:
  def apply[F[_]: MonadThrow](dao: UserRepositoryDao)(using Ask[F, Config]): UserRepository[F] = new UserRepository[F]:
    override def findAll: F[List[User]] = summon[Ask[F, Config]].reader(c => dao.findAll(c))
//      task"""
//          Реализуйте обёртку над методом findAll в dao, используя конфиг из Ask
//          """ (2, 1)
    override def create(name: UserName, age: Age, friends: Set[UserId]): F[User] = summon[MonadThrow[F]].flatMap(
      summon[Ask[F, Config]].ask)(c =>
        summon[MonadThrow[F]].fromEither(
          dao.create(name, age, friends)(c).left.map(e => new RuntimeException(s"User ${e.name} already exists"))
        )
      )
//  task"""
//          Реализуйте обёртку над методом create в dao, используя конфиг из Ask и обработав ошибку из Either
//          при помощи ApplicativeError
//          """ (2, 2)
    override def delete(userId: UserId): F[Unit] = summon[MonadThrow[F]].flatMap(summon[Ask[F, Config]].ask)(c =>
      summon[MonadThrow[F]].fromEither(
        dao.delete(userId)(c).left.map(e => new RuntimeException(s"User ${e.id} does not exist"))
      )
    )
//  task"""
//          Реализуйте обёртку над методом delete в dao, используя конфиг из Ask и обработав ошибку из Either
//          при помощи ApplicativeError
//          """ (2, 3)
    override def update(user: User): F[Unit] = summon[MonadThrow[F]].flatMap(summon[Ask[F, Config]].ask)(c =>
      summon[MonadThrow[F]].fromEither(
        dao.update(user)(c).left.map(e => new RuntimeException(s"User ${e.id} does not exist"))
      )
    )
//  task"""
//          Реализуйте обёртку над методом update в dao, используя конфиг из Ask и обработав ошибку из Either
//          при помощи ApplicativeError
//          """ (2, 4)
