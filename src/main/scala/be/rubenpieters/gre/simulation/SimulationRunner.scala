package be.rubenpieters.gre.simulation

import be.rubenpieters.gre.engine.{EngineFactory, EngineRunner}

/**
  * Created by rpieters on 19/06/2016.
  */
class SimulationRunner(engineFactory: EngineFactory) {
  def runXSimulations(x: Long) = {
    val result = (1L to x).map { i =>
      println("SIMULATION " + i)
      val engine = engineFactory.newEngineRunner(i)
      engine.runUntilEndConditionReached()
      engine.entityManager.entityMap
    }
    println(result)
  }
}
