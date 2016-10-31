package be.rubenpieters.model.rps

import be.rubenpieters.util.Inverse

/**
  * Created by ruben on 31/10/2016.
  */
sealed trait RpsWinState
case object Win extends RpsWinState
case object Loss extends RpsWinState
case object Tie extends RpsWinState

object RpsWinState {
  implicit object RpsWinInverse extends Inverse[RpsWinState] {
    override def inverse(a: RpsWinState): RpsWinState = a match {
      case Win => Loss
      case Loss => Win
      case Tie => Tie
    }
  }
}