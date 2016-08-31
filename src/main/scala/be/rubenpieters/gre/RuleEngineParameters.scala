package be.rubenpieters.gre

import scala.util.Random

/**
  * Created by ruben on 29/08/2016.
  */
case class RuleEngineParameters(rng: Random) {

}

object RuleEngineParameters {
  def newParameters: RuleEngineParameters = {
    RuleEngineParameters(
      new Random()
    )
  }
}