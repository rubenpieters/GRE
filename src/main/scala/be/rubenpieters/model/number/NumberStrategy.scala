package be.rubenpieters.model.number

import be.rubenpieters.model.{RunnerState, Strategy}
import cats.data.State

/**
  * Created by ruben on 11/11/2016.
  */
object NumberStrategy extends Strategy[NumInput, NumOutput, RunnerState] {
  override def getAction(input: NumInput): State[RunnerState, NumOutput] = {
    State.pure(input._1.sum)
  }
}
