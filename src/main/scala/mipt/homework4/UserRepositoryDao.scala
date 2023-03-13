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
    task"""
          Реализуйте методы ниже так, чтобы они вызывали методы из UserRepositoryDao с конфигом из Ask
          и обрабатывали возвращаемые Either в стиле ApplicativeError
          """ (2, 1)
    override def findAll: F[List[User]] = ???
    override def create(name: UserName, age: Age, friends: Set[UserId]): F[User] = ???
    override def delete(userId: UserId): F[Unit] = ???
    override def update(user: User): F[Unit] = ???
