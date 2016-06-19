package be.rubenpieters.gre.main

import be.rubenpieters.gre.engine.examples.simplegame.SimpleGameEngine
import be.rubenpieters.gre.simulation.SimulationRunner

/**
  * Created by rpieters on 14/05/2016.
  */
object AdHocTestMain extends App {
  val engineRunner = SimpleGameEngine.simpleEngine

  engineRunner.runUntilEndConditionReached()
}

object AdHocTestMainSimulation extends App {
  val simulationRunner = new SimulationRunner(SimpleGameEngine.simpleEngineFactory,
    entityManager => {
      (entityManager.getEntity("ally").properties.getOrElse("HP", Long.MinValue),
        entityManager.getEntity("enemy").properties.getOrElse("HP", Long.MinValue)
        )
    }
  )
  simulationRunner.runXSimulations(10)
}
