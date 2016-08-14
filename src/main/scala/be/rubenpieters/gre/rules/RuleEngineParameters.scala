package be.rubenpieters.gre.rules

import scala.util.Random

/**
  * Created by rpieters on 29/05/2016.
  */
case class RuleEngineParameters(rng: Random) {

}

object RuleEngineParameters {
  def default = {
    RuleEngineParameters(new Random())
  }
}