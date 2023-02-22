//package mipt.functors
//
//// Proving Laws for Option instance
//given Functor[Option] = new Functor[Option] {
//  override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
//
//  def lift[A, B](f: A => B): Option[A] => Option[B] =
//    case None => None
//    case Some(x) => Some(f(x))
//}
//
//// 1) lift(identity) = identity
//// Option[x] => Option[x]
///**
//  lift(identity)(None) = None
//  identity(None) = None
//
//  lift(identity)(Some(x)) = Some(identity(x)) = Some(x)
//  identity(Some(x)) = Some(x)
// */
//
//def foo[A, B, C](f: A => B, g: B => C): A => C = g compose f
//
//// 2) (lift(f) compose lift(g)) = lift (f compose g)
///**
//   (lift(f) compose lift(g))(None) = lift(f)(lift(g)(None)) =
//   = lift(f)(None) = None
//   lift(f compose g)(None) = None
//
//   (lift(f) compose lift(g))(Some(x)) = lift(f)(lift(g)(Some(x))) =
//   = lift(f)(Some(g(x))) = Some(f(g(x)))
//   lift(f compose g)(Some(x)) = Some((f compose g)(x)) = Some(f(g(x)))
//*/
//
//// Нарушаются ли законы для таких инстансов?
//
////def lift[A, B](f: A => B): Option[A] => Option[B] =
////  case None => None
////  case Some(_) => None
////
////// 1) lift(identity) = identity
////// lift(identity)(Some(x)) = None
////// identity(Some(x)) = Some(x)
//
//
////def question2[A, B](f: A => B): Option[A] => Option[B] =
////  case None => None
////  case Some(x: B) => Some(x)
////  case Some(x) => Some(f(x))
//
//def question2[A, B](f: A => B): Option[A] => Option[B] =
//  case None => None
//  case Some(x: Int) => Some(f((x + 1).asInstanceOf[A]))
//  case Some(x) => Some(f(x))
//
//// Proving Laws for List instance
//
//given Functor[List] = new Functor[List] {
//  override def map[A, B](fa: List[A])(f: A => B): List[B] = lift(f)(fa)
//
//  def lift[A, B](f: A => B): List[A] => List[B] =
//    case Nil => Nil
//    case h :: t => f(h) :: lift(f)(t)
//}
//
//// 1) lift(identity) = identity
///**
//  induction by list length:
//    base: Nil = Nil
//    step: lift(identity)(h :: ListN) = identity(h) :: lift(identity)(ListN) = h :: ListN
// */
//
//// 2) (lift(f) compose lift(g)) = lift (f compose g)
///**
// * induction by list length:
// *   base: Nil = Nil
// *   step: ((lift(f) compose (lift(g)))) (ListN) = (lift(f)(g(h) :: lift(g)(ListN))) =
// *     = f(g(h)) :: lift(f)(lift(g)(ListN)) = f(g(h)) :: (lift(f) compose lift(g))(ListN) =
// *     = (f compose g)(h) :: (f compose g)(ListN)
// */
//
//// Meaning of laws: Functor keeps structure!
//// Tree example
//
//sealed trait Tree[A]
//case class Leaf[A](v: A) extends Tree[A]
//case class Node[A](v: A, left: Tree[A], right: Tree[A]) extends Tree[A]
//
//given Functor[Tree] = new Functor[Tree]:
//  override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] =
//    fa match
//      case Leaf(x) => Leaf(f(x))
//      case Node(x, l, r) => Node(f(x), map(l)(f), map(r)(f))
//
//object App3 extends App {
//  val someTree: Tree[String] = Node(
//    "foo",
//    Node(
//      "kkke",
//      Node("x", Leaf("abra"), Leaf("kadabra")),
//      Leaf("dddd"),
//    ),
//    Node(
//      "kkke",
//      Node("x", Leaf("abra"), Node(
//        "kkke",
//        Node("xdddd", Leaf("abra"), Leaf("kadabra")),
//        Leaf("dddd"))
//      ),
//      Leaf("dddd")
//    )
//  )
//
//  println(lengthAny(someTree))
//}
//
//// "Polynomial" functors
///**
// * Option[A] = 1 + A
// * List[A] = 1 + (A * List[A]) = 1 + (A * (1 + (A * List[A]))) + ... =
// * = 1 + A + A ^ 2 + A ^ 3 + ...
// *
// * Tree[A] = 1 + (A * Tree[A] * Tree[A]) =
// * = 1 + (A * (1 + (A * (Tree[A] ^ 2))) * (1 + (A * Tree[A] * Tree[A]))) =
// * = 1 + (A + A ^ 2 * Tree[A]^2)^2 =
// * = 1 + A^2 + 2 * A^3 * Tree[A]^2 + A^4*Tree[A]^4 =
// * = 1 + A^2 + 2 * A^3 + ...
// *
// * F[A] = a_0 + a_1 * A + a_2 * A ^ 2 + ....
// *
// *
// */
