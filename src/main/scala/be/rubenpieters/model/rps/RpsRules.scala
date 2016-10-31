package be.rubenpieters.model.rps

import simulacrum._

/**
  * Created by ruben on 31/10/2016.
  */
@typeclass trait RpsRules[A] {
  def beats[B : RpsRules](a: A, b: B): RpsWinState
}
