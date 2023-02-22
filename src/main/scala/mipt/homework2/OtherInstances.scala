package mipt.homework2

import cats.Functor

trait OtherInstances:
  type Arr[-C, +A] = C => (List[A] => C) => A

  given [C]: Functor[Arr[C, *]] =
    task"Реализуйте инстанс Functor для Arr[C, *] (по второму аргументу)" (4, 0)

  given [F[_]: Functor, G[_]: Functor]: Functor[[x] =>> F[G[x]]] =
    task"""Реализуйте инстанс для композиции функторов и
           докажите законы функтора для этого инстанса""" (4, 1)
