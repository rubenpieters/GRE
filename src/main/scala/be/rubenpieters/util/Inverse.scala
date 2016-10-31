package be.rubenpieters.util

import simulacrum.typeclass

/**
  * Created by ruben on 31/10/2016.
  */
@typeclass trait Inverse[A] {
  def inverse(a: A): A
}
