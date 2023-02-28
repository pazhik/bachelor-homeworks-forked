package mipt.monads

import mipt.functors.monoidSyntax.|+|
import mipt.functors.Monoid
import mipt.functors.functorSyntax.map
import mipt.monads.Pointed.syntax.pure
import mipt.monads.State.{T, get, put}
import mipt.monads.syntax.*

import java.util.UUID
import scala.annotation.tailrec
import scala.util.Try

// classic monad examples

// identity
given Monad[Id] = new Monad[Id] {
  def flatMap[A, B](fa: A)(f: A => B): B = f(fa)
  def pure[A](a: A): A = a
}

// reader (context)
type Reader[-Ctx, +A] = Ctx => A

given [C]: Monad[Reader[C, *]] = new Monad[Reader[C, *]] {
  def flatMap[A, B](fa: C => A)(f: A => (C => B)): C => B = r => f(fa(r))(r)
  def pure[A](a: A): Reader[C, A] = _ => a
}

object Reader:
  def ask[Ctx]: Reader[Ctx, Ctx] = identity
  def asks[Ctx, SubCtx](f: Ctx => SubCtx): Reader[Ctx, SubCtx] = ask.map(f)
  def local[Ctx](modify: Ctx => Ctx): Reader[Ctx, *] ~> Reader[Ctx, *] =
    [A] => (r: Reader[Ctx, A]) => r compose modify

case class Diagnostic(traceId: UUID, spanId: UUID)
case class Input()

def handler(input: Input): Reader[Diagnostic, Unit] =
  for {
    d <- Reader.ask[Diagnostic]
    _ = println(s"Handling request: traceId: ${d.traceId}, spanId: ${d.spanId}")
// .... f(input)
  } yield ()

def runNextRequest(input: Input): Reader[Diagnostic, Unit] =
  for {
    spanId <- Reader.asks[Diagnostic, UUID](_.spanId)
    newSpan = UUID.randomUUID()
    _ = println(s"last spanId: ${spanId}, new spanId: $newSpan")
    _ <- Reader.local[Diagnostic](_.copy(spanId = newSpan))(handler(input))
  } yield ()

@main def _4_1(): Unit = {
  println(runNextRequest(Input())(Diagnostic(UUID.randomUUID(), UUID.randomUUID())))
}

// show asks2, apply pattern

/// writer (log)
type Writer[+W, +A] = (A, W)

given [W: Monoid]: Monad[Writer[W, *]] = new Monad[Writer[W, *]] {
  override def flatMap[A, B](fa: (A, W))(f: A => (B, W)): (B, W) =
    val (b, acc) = f(fa._1)
    (b, fa._2 |+| acc)

  override def pure[A](a: A): (A, W) = (a, Monoid[W].empty)
}

object Writer:
  def tell[W](msg: W): Writer[W, Unit] = ((), msg)
  def listen[A, W]: Writer[W, A] => Writer[W, (A, W)] =
    (a, w) => ((a, w), w)
  def modify[A, W](w: Writer[W, A])(f: W => W): Writer[W, A] =
    (w._1, f(w._2))

opaque type Vegetable = String

def addVegetable(veg: Vegetable, count: Int, price: Double): Writer[Double, Vegetable] =
  val total = count * price
  Writer.tell(total).map(_ => veg)

@main def _4_2(): Unit = {
  val firstBucket = for {
    p <- addVegetable("potato", 3, 10)
    c <- addVegetable("carrot", 1, 20)
    t <- addVegetable("tomato", 2, 15)
  } yield List(p, c, t)

  println(firstBucket)

  val sndBucket = for {
    a <- addVegetable("apple", 3, 10)
    o <- addVegetable("orange", 1, 20)
  } yield List(a, o)

  println(sndBucket)

  val res = for {
    f <- Writer.listen(firstBucket)
    s <- Writer.listen(sndBucket)
  } yield List(f, s)

  println(res)

  println(Writer.modify(res)(_ * 0.9))
}

// State monad

object State extends StateOps:
  opaque type T[S, +A] = S => (A, S)

  def apply[S, A](f: S => (A, S)): T[S, A] = f

  given [S]: Monad[T[S, *]] = new Monad[T[S, *]] {
    def flatMap[A, B](fa: S => (A, S))(f: A => S => (B, S)): S => (B, S) =
      old =>
        val (a, s) = fa(old)
        f(a)(s)

    def pure[A](a: A): T[S, A] = s => (a, s)
  }

  extension [S, A] (t: T[S, A])
    def run(init: S): (A, S) = t(init)

  def get[S]: T[S, S] = s => (s, s)
  def getAnd[S, S2](f: S => S2): T[S, S2] = s => (f(s), s)
  def put[S](s: S): T[S, Unit] = _ => ((), s)


type State[S, +A] = State.T[S, A]

trait StateOps:
  def modify[S](f: S => S): T[S, S] =
    for {
      old <- State.get[S]
      newS = f(old)
      _ <- State.put[S](newS)
    } yield newS

def zipIndex[A](as: List[A]): List[(Int, A)] =
  as.foldLeft(
    List[(Int, A)]().pure[State[Int, *]]
  )((acc: State[Int, List[(Int, A)]], a) =>
    for {
      xs <- acc
      n <- State.modify[Int](_ + 1)
    } yield (n, a) :: xs
  ).run(0)._1.reverse

@main def _4_3(): Unit =
//  println(zipIndex(List(4, 2, 4)))
  println(zipIndex((1 to 100000).toList))

