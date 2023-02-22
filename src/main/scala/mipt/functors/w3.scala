package mipt.functors

//lift(identity) = identity
//(lift(f) compose lift(g)) = lift (f compose g)
def liftQ1[A, B](f: A => B): Option[A] => Option[B] =
  _ => None

// Нарушится ли закон для такого инстанса
def question2[A, B](f: A => B): Option[A] => Option[B] =
  case None => None
  case Some(x: Int) => Some(f((x + 1).asInstanceOf[A]))
  case Some(x) => Some(f(x))

// Laws application 2: optimizations
// x.map(g).map(f) = x.map(f compose g)

// Meaning of laws: Functor keeps structure!
// Tree example

sealed trait Tree[A]
case class Leaf[A](v: A) extends Tree[A]
case class Node[A](v: A, left: Tree[A], right: Tree[A]) extends Tree[A]

given Functor[Tree] = new Functor[Tree]:
  override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] =
    fa match
      case Leaf(x) => Leaf(f(x))
      case Node(x, l, r) => Node(f(x), map(l)(f), map(r)(f))

@main def _3test1(): Unit = {
  val someTree: Tree[String] = Node(
    "foo",
    Node(
      "kkke",
      Node("x", Leaf("abra"), Leaf("kadabra")),
      Leaf("dddd"),
    ),
    Node(
      "kkke",
      Node("x", Leaf("abra"), Node(
        "kkke",
        Node("xdddd", Leaf("abra"), Leaf("kadabra")),
        Leaf("dddd"))
      ),
      Leaf("dddd")
    )
  )

  println(lengthAny(someTree))
}

// "Polynomial" functors
/**
 * Option[A] = 1 + A
 * List[A] = 1 + (A * List[A]) = 1 + (A * (1 + (A * List[A]))) + ... =
 * = 1 + A + A ^ 2 + A ^ 3 + ...
 *
 * Tree[A] = 1 + (A * Tree[A] * Tree[A]) =
 * = 1 + (A * (1 + (A * (Tree[A] ^ 2))) * (1 + (A * Tree[A] * Tree[A]))) =
 * = 1 + (A + A ^ 2 * Tree[A]^2)^2 =
 * = 1 + A^2 + 2 * A^3 * Tree[A]^2 + A^4*Tree[A]^4 =
 * = 1 + A^2 + 2 * A^3 + ...
 *
 * F[A] = a_0 + a_1 * A + a_2 * A ^ 2 + ....
 *
 *
 */
