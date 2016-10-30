package be.rubenpieters.model.rps

import be.rubenpieters.model.Strategy

/**
  * Created by ruben on 30/10/16.
  */
object UniformRPSStrategy extends Strategy[RpsInput, RpsOutput] {
  override def getAction(input: RpsInput): (Float, Float, Float) = normalize((1,1,1))

  def normalize(rpsOutput: RpsOutput): RpsOutput = {
    val total = rpsOutput._1 + rpsOutput._2 + rpsOutput._3
    (rpsOutput._1 / total, rpsOutput._2 / total, rpsOutput._3 / total)
  }
}
