package be.rubenpieters.model.rps

import be.rubenpieters.model.{RunnerState, Strategy}
import cats.data.State

/**
  * Created by ruben on 30/10/16.
  */
object RpsUniformStrategy extends Strategy[RpsInput, RpsOutput, RunnerState] {
  override def getAction(input: RpsInput) = State[RunnerState, RpsOutput] { rng =>
    val (newRng, choice) = rng.chooseOne(Map(Rock -> 1, Paper -> 1, Scissors -> 1))
    (newRng, choice)
  }
}
