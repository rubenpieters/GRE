package be.rubenpieters.model

import cats.data.State

/**
  * Created by rpieters on 30/10/2016.
  */
trait Strategy[I, O, S] {
  def getAction(input: I): State[S, O]
}

case class StoredStrategy[I, O, S](playerId: Long, gameId: Long, name: String, strategy: Strategy[I, O, S])