# Writer

```scala
import cats.Monoid
import mipt.monad.instances.Writer

case class Writer[W, A](log: W, value: A)
case class WriterT[F[_], W: Monoid, A](value: F[Writer[W, A]])
```

```
F - Monad
A - Type
a - Value of type A
W - Type: Monoid
log - Value of type W
fa  - Value of type WriterT[F, W, A] = WriterT(log, a)

(1)  fa.pure[WriterT[F, *, *]].flatten[WriterT[F, *, *]] = fa

fa.pure = F(Writer(Monoid[W].empty, F(Writer(log, a)))) => 
    => fa.pure.flatten = F(Writer(Monoid[W].empty, F(Writer(log, a)))).flatMap(identity) =
    = F(Writer(Monoid[W].combine(Monoid[W].empty, log), identity(a))) =
    = F(Writer(log, a))


(2) (_.pure[WriterT[F, *, *]].lift[WriterT[F, *, *]])(fa).flatten[WriterT[F, *, *]] = fa

_.pure[WriterT[F, *, *]] = x => F(Writer(Monoid[W].empty, x))
f: A => WriterT[F, W, A] - function
f.lift[WriterT[F, *, *]] = F(Writer(l, x)) => F(Writer(l, f(x)))
_.pure.lift = F(Writer(l, x)) => F(Writer(l, F(Writer(Monoid[W].empty, x))))

fa => _.pure.lift(fa) = F(Writer(log, F(Writer(Monoid[W].empty, a)))) => _.pure.lift(fa).flatten = F(Writer(log, a))


(3) fffa - Value of type WriterT[WriterT[WriterT[F, *, *], *, *], W, A] 
     = F(Writer(log1, F(Writer(log2, F(Writer(log3, a))))))
fffa.flatten.flatten = _.flatten.lift(fffa).flatten

_.flatten = F(Writer(l1, F(Writer(l2, x)))) => F(Writer(l1 |+| l2, x))
_.flatten.lift = F(Writer(l1, F(Writer(l2, F(Writer(l3, x)))))) => F(Writer(l1, F(Writer(l2 |+| l3, x)))

fffa.flatten = F(Writer(log1, F(Writer(log2 |+| log3, a)))) 
            => fffa.flatten.flatten = F(Writer(log1 |+| log2 |+| log3, a))
_.flatten.lift(fffa) = F(Writer(log1 |+| log2, F(Writer(log3, a)))) 
            => _.flatten.lift(fffa).flatten = F(Writer(log1 |+| log2 |+| log3, a))
```