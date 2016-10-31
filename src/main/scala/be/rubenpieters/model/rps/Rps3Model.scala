package be.rubenpieters.model.rps

import RpsRules.ops._
import be.rubenpieters.util.Inverse.ops._

/**
  * Created by ruben on 31/10/2016.
  */
sealed trait Rps3Model
case object Rock extends Rps3Model {
  implicit object RockRules extends RpsRules[this.type] {
    override def beats[B: RpsRules](a: Rock.this.type, b: B): RpsWinState = b match {
      case Rock => Tie
      case _ => b.beats(a).inverse
    }
  }
}

case object Paper extends Rps3Model {
  implicit object PaperRules extends RpsRules[this.type] {
    override def beats[B: RpsRules](a: Paper.this.type, b: B): RpsWinState = b match {
      case Rock => Win
      case Paper => Tie
      case _ => b.beats(a).inverse
    }
  }
}

case object Scissors extends Rps3Model {
  implicit object ScissorsRules extends RpsRules[this.type] {
    override def beats[B: RpsRules](a: Scissors.this.type, b: B): RpsWinState = b match {
      case Rock => Loss
      case Paper => Win
      case Scissors => Tie
      case _ => b.beats(a).inverse
    }
  }
}