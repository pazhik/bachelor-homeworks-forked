# Постановка задачи

Это задание предназначено для того, чтобы закрепить усвоение материала лекции, научиться работать с
трансформерами монад.

1) (ToDo практическое задание)

2) Выбрать одну любимую монаду из `Reader, Writer, State, Either` и доказать для её трансформера законы:
```scala
F - Monad
∀ A - Type
   pure[F[A]]    flatten[A]
F[A] ----> F[F[A]] ----> F[A]
flatten[A] compose pure[F[A]] = id[F[A]]
  lift(pure[A])  flatten[A]
F[A] ----> F[F[A]] ----> F[A]
flatten[A] compose lift(pure[A]) = id[F[A]]
                    flatten[F[A]]
              F[F[F[A]]] ----> F[F[A]]
                  |                |
lift(flatten[A])  |                |  flatten[A]
                  V                V
                F[F[A]]    ---->   F[A]
                        flatten[A]
flatten[A] compose flatten[F[A]] = flatten[A] compose lift(flatten[A])
```

В качестве подсказки можно использовать доказательство для OptionT:
```
F - Monad
A - Type
a - Value of type A
fa - Value of type OptionT[F, A]

fa.pure[OptionT[F, *]].flatten[OptionT[F, *]] = fa
fa match
  case F(Some(a)) => fa.pure = F(Some(F(Some(a)))) => fa.pure.flatten = F(Some(F(Some(a)))).flatMap(identity) = F(Some(a))
  case F(None) => fa.pure = F(Some(F(None))) => fa.pure.flatten = F(Some(F(None))).flatMap(identity) = F(None)

(_.pure[OptionT[F, *]].lift[OptionT[F, *]])(fa).flatten[OptionT[F, *]] = fa
_.pure[OptionT[F, *] = x => F(Some(x))
f.lift[OptionT[F, *]] = F(Some(x)) => F(Some(f(x)))
                        F(None) => F(None)
_.pure.lift = F(Some(x)) => F(Some(F(Some(x))))
              F(None) => F(None)
fa match
  case F(Some(a)) => _.pure.lift(fa) = F(Some(F(Some(a)))) => _.pure.lift(fa).flatten = F(Some(a))
  case F(None) => _.pure.lift(fa) = F(None) => _.pure.lift(fa).flatten = F(None)

fffa - Value of type OptionT[OptionT[OptionT[F, *], *], A]
fffa.flatten.flatten = _.flatten.lift(fffa).flatten
_.flatten = F(Some(F(Some(a)))) => F(Some(a))
            F(Some(F(None))) => F(None)
            F(None) => F(None)
_.flatten.lift = F(Some(F(Some(F(Some(a)))))) => F(Some(F(Some(a)))
                 F(Some(F(Some(F(None))))) => F(Some(F(None)))
                 F(Some(F(None))) => F(Some(F(None)))
                 F(None) => F(None)
fffa match
  case F(Some(F(Some(F(Some(a)))))) => fffa.flatten = F(Some(F(Some(a)))) => fffa.flatten.flatten = F(Some(a))
                                       _.flatten.lift(fffa) = F(Some(F(Some(a)))) => _.flatten(fffa).flatten = F(Some(a))
  case F(Some(F(Some(F(None))))) => fffa.flatten = F(Some(F(None))) => fffa.flatten.flatten = F(None)
                                    _.flatten.lift(fffa) = F(Some(F(None))) => _.flatten.lift(fffa).flatten = F(None)
  case F(Some(F(None))) => fffa.flatten = F(None) => fffa.flatten.flatten = F(None)
                           _.flatten.lift(fffa) = F(Some(F(None))) => _.flatten.lift(fffa).flatten = F(None)
  case F(None) => fffa.flatten = F(None) => fffa.flatten.flatten = F(None)
                  _.flatten.lift(fffa) = F(None) => _.flatten.lift(fffa).flatten = F(None)
```