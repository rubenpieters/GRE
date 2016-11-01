package be.rubenpieters.storage

import be.rubenpieters.model.Strategy

/**
  * Created by ruben on 1/11/2016.
  */
case class StoredStrategy(playerId: Long, gameId: Long, name: String, strategy: Array[Byte])
