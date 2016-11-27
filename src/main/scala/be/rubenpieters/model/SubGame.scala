package be.rubenpieters.model

import cats.data.State

/**
  * input stays the same in the subgame chain
  * state is carried on from the previous to the next subgame in the subgame chain
  *
  * Created by ruben on 26/11/2016.
  */
case class SubGame[I, S, O](name: String, input: I, operation: I => State[S, Either[O, SubGame[I, S, O]]])

object SubGame {
//  def run(runner: )
}

sealed trait GameRunningState
case object Idle extends GameRunningState
case object Running extends GameRunningState
case object Finished extends GameRunningState

