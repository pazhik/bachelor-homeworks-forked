package mipt.functors

// Recap: typeclass

// How to make it abstract?
def sumListInt(x: List[Int]): Int             = x.foldLeft(0)(_ + _)
def sumListString(x: List[String]): String    = x.foldLeft("")(_ + _)
def sumListList[A](x: List[List[A]]): List[A] = x.foldLeft(List.empty)(_ ++ _)

@main def _1test1(): Unit = {
  println(sumListInt(List(1, 2, 3)))
  println(sumListString(List("foo", "bar", "kek")))
  println(sumListList(List(List(1, 3), List(2, 3), Nil)))
}

trait Monoid[A] {
  def empty: A
  def combine(x: A, y: A): A // |+|
  // laws:
  // neutral element:  empty |+| x = x |+| empty = x
  // associativity: |x| + (|y| + |z|) = (|x| + |y|) + |z| = |x| + |y| + |z|
}

trait MonoidInstances {
  // implicit val given_Monoid_Int: Monoid[Int] = new Monoid[Int] {..}
  given Monoid[Int] = new Monoid[Int] {
    def empty: Int                   = 0
    def combine(x: Int, y: Int): Int = x + y
  }

  given Monoid[String] = new Monoid[String] {
    def empty: String                   = ""
    def combine(x: String, y: String): String = x + y
  }

  // implicit def given_Monoid_List_A[A]: Monoid[List[A]] = new Monoid[List[A]] {..}
  given [A]: Monoid[List[A]] = new Monoid[List[A]] {
    def empty: List[A]                           = List()
    def combine(x: List[A], y: List[A]): List[A] = x ++ y
  }

  given Monoid[Double] = new Monoid[Double] {
    def empty: Double = 0
    def combine(x: Double, y: Double): Double = x + y
  }
}

// Adding syntax
object Monoid extends MonoidInstances {
  @inline
  def apply[A](using M: Monoid[A]): Monoid[A] = M
}

//def sumList[A](list: List[A])(implicit M: Monoid[A]): A = ...
def sumList[A: Monoid](list: List[A]): A =
  list.foldLeft(Monoid[A].empty)(Monoid[A].combine)

@main def _1test2(): Unit = {
  println(sumList(List(1, 2, 3)))
  println(sumList(List("foo", "bar", "kek")))
  println(sumList(List(List(1, 3), List(2, 3), Nil)))
}

object monoidSyntax {
  // implicit class MonoidSyntax[A](a: A) extends AnyVal {
  //   def |+|...
  // }
  extension[A] (x: A)
    def |+|(y: A)(using M: Monoid[A]): A = M.combine(x, y)
}

//val x = (2 |+| 4)
import monoidSyntax.*

def sumListP[A: Monoid](list: List[A]): A =
  list.foldLeft(Monoid[A].empty)(_ |+| _)

// Why?
// - less boilerplate (DRY), harder to make an error
// - better understanding
