package be.rubenpieters.model

import cats.data.State

/**
  * Created by ruben on 26/11/2016.
  */
case class SubGame[I, S, O](name: String, input: I, valueState: S, operation: I => State[S, O], runningState: GameRunningState) {

}

object SubGame {
//  def run(runner: )
}

sealed trait GameRunningState
case object Idle extends GameRunningState
case object Running extends GameRunningState
case object Finished extends GameRunningState

