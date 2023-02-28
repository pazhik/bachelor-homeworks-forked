package mipt.monads

import scala.util.Try
import mipt.functors.Functor
import mipt.functors.functorSyntax.map
import mipt.monads.syntax.*

// laws sense

// x.flatMap(pure) = x
// pure(x).flatMap(f) = f(x)
// a.flatMap(f).flatMap(g) = a.flatMap(f(_).flatMap(g))

// 1 / (1 - 1 / (1 - x))
def inner(x: Double) = for {
  rel <- safeDiv (1, 1 - x, "1 - x")
  y = 1.0 - rel
  res <- safeDiv (1, y, "1 - 1 / (1 - x)")
} yield res

val outer = for {
  x <- inner(2)
  y <- inner(3)
} yield x + y

// using referential transparency
val outer2 = for {
  x <- for {
    rel <- safeDiv(1, 1 - 2, "1 - x")
    y = 1 - rel
    res <- safeDiv(1, y, "1 - 1 / (1 - x)")
  } yield res
  y <- for {
    rel <- safeDiv(1, 1 - 3, "1 - x")
    y = 1.0 - rel
    res <- safeDiv(1, y, "1 - 1 / (1 - x)")
  } yield res
} yield x + y

// using associativity
val outer3 = for {
  rel <- safeDiv(1, 1 - 2, "1 - x")
  y = 1 - rel
  res <- safeDiv(1, y, "1 - 1 / (1 - x)")
  rel2 <- safeDiv(1, 1 - 3, "1 - x")
  y2 = 1 - rel2
  res2 <- safeDiv(1, y2, "1 - 1 / (1 - x)")
} yield res + res2

@main def _3(): Unit = {
  println(outer)
  println(outer2)
  println(outer3)
}
