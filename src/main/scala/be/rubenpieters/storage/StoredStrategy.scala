package be.rubenpieters.storage

import be.rubenpieters.model.Strategy

/**
  * Created by ruben on 1/11/2016.
  */
case class StoredStrategy[I, O, S](playerId: Long, gameId: Long, name: String, strategy: Strategy[I, O, S])
