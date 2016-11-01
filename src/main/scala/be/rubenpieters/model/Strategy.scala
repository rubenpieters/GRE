package be.rubenpieters.model

import be.rubenpieters.storage.{StoredGame, StoredStrategy}
import cats.data.State

/**
  * Created by rpieters on 30/10/2016.
  */
trait Strategy[I, O, S] {
  def getAction(input: I): State[S, O]
}

//object Strategy {
//  def fromStored(storedStrategy: StoredStrategy, storedGame: StoredGame) = {
//    storedStrategy.strategy
//  }
//}