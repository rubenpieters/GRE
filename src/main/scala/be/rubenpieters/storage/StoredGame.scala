package be.rubenpieters.storage

import be.rubenpieters.model.RunnerState
import be.rubenpieters.model.rps.{RpsInput, RpsOutput}

/**
  * Created by ruben on 1/11/2016.
  */
case class StoredGame[I, O, S](gameId: Long, name: String)

object Games {
  val rps = StoredGame[RpsInput, RpsOutput, RunnerState](
    1
    , "rps"
  )
}
