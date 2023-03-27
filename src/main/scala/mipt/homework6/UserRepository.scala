package mipt.homework6

import mipt.utils.Homeworks._
import zio.{URIO, ZIO}
import User._, UserErrors._


import zio.{URIO, ZIO}

object UserRepository {

    case class Config(chunkSize: Int)
    
    def apply(dao: UserRepositoryDao): UserRepository =
        new UserRepository {
            override def findAll: URIO[Config, List[User]] = task"""
                Реализуйте обёртку над методом findAll в dao, используя конфиг из R
                """ (2, 1)
            override def create(name: UserName, age: Age, friends: Set[UserId] = Set.empty): ZIO[Config, UserAlreadyExists, User] = task"""
                Реализуйте обёртку над методом create в dao, используя конфиг из R и обработав ошибку из Either
                """ (2, 2)
            override def delete(userId: UserId): ZIO[Config, UserDoesNotExists, Unit] = task"""
                Реализуйте обёртку над методом delete в dao, используя конфиг из R и обработав ошибку из Either
                """ (2, 3)
            override def update(user: User): ZIO[Config, UserDoesNotExists, Unit] = task"""
                Реализуйте обёртку над методом update в dao, используя конфиг из R и обработав ошибку из Either
                """ (2, 4)
                }
            
}

import User._, UserErrors._, UserRepository._

trait UserRepository {
    def findAll: URIO[Config, List[User]]
    def create(name: UserName, age: Age, friends: Set[UserId] = Set.empty): ZIO[Config, UserAlreadyExists, User]
    def delete(userId: UserId): ZIO[Config, UserDoesNotExists, Unit]
    def update(user: User): ZIO[Config, UserDoesNotExists, Unit]
}

