package be.rubenpieters.gre.simulation

import be.rubenpieters.gre.engine.EngineFactory
import be.rubenpieters.gre.entity.EntityManager

/**
  * Created by rpieters on 19/06/2016.
  */
class SimulationRunner[T](engineFactory: EngineFactory, simulationResult: EntityManager => T) {
  def runXSimulations(x: Long) = {
    val result = (1L to x).map { i =>
      println("SIMULATION " + i)
      val engine = engineFactory.newEngineRunner(i)
      engine.runUntilEndConditionReached()
      simulationResult.apply(engine.entityManager)
    }
    println(result)
  }
}
