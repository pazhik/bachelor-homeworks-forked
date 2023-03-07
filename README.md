# Постановка задачи

Это задание предназначено для того, чтобы закрепить усвоение материала лекции, научиться описывать монады и работать с
ними.

1) (UserRepository) В задании смоделирована потребность реализации простейшей производной монады, которая представляет из себя
операцию чтения из репозитория, в интерфейсе которого присутствует абстракция на эффект операции.
Необходимо реализовать все неимплементированные (???) методы согласно комментариям в коде таким образом,
чтобы все тесты выполнялись без ошибок.

2) Выбрать одну любимую монаду из `Reader, Writer, State, Either` и доказать для нее законы:
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

3) `F[_]` - монада. Доказать, что из 3го законы монады через `flatten` и `lift` следует 3ий закон монады через `flatMap`.
То есть из
```scala
  ∀ A - Type

  flatten[A] compose flatten[F[A]] = flatten[A] compose lift(flatten[A])
```
следует:
```scala
  ∀ A, B, C - Types
  ∀ f: A => F[B], g: B => F[C]
  ∀ x: F[A]

  x.flatMap(f).flatMap(g) = x.flatMap(a => f(a).flatMap(g))
```
Используя натуральность `flatten`.
Закон для натурального преобразования:
```scala
  F, G - Functors
  trans[A]: F[A] => G[A] // trans: F ~> G

  ∀ A, B - Types
  ∀ f: A => B
  trans[B] compose Functor[F].lift(f) = Functor[G].lift(f) compose trans[A]
```

Задачи на доказательства можно присылать в любой удобной форме - фотографии записей от руки, комментарии в коде, код на Coq или другом теорем прувере.
