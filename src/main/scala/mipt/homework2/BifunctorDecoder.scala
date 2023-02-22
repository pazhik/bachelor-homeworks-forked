package mipt.homework2

import cats.{Bifunctor, Functor}
import mipt.utils.Homeworks.TaskSyntax

//trait BifunctorDecoder[E, T]:
//  def apply(raw: String): BifunctorDecoder.Result[E, T]
//
object BifunctorDecoder
//  type Result[E, T] = Either[E, T]
//
//  task"Реализуйте Bifunctor для Decoder, используя Either.left проекцию"
//  given Bifunctor[BifunctorDecoder] = new Bifunctor[BifunctorDecoder]:
//    override def bimap[A, B, C, D](fab: BifunctorDecoder[A, B])(f: A => C, g: B => D): BifunctorDecoder[C, D] =
//      (raw: String) => fab.apply(raw).map(g).left.map(f)
//
//object DDecoder:
//
//  trait Error
//  
//  type DDecoder[T] = BifunctorDecoder[Error, T]
//  
//  task"Реализуйте Functor для Decoder"
//  given Functor[DDecoder] = new Functor[DDecoder]:
//    override def map[A, B](fa: DDecoder[A])(f: A => B): DDecoder[B] = ???

