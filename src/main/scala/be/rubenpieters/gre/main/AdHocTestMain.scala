package be.rubenpieters.gre.main

import be.rubenpieters.gre.engine.examples.simplegame.SimpleGameEngine
import be.rubenpieters.gre.simulation.SimulationRunner
import main.scala.be.rubenpieters.gre.engine.examples.blackjack.SimpleBlackjackEngine

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
object AdHocTestBlackjackSimulation extends App {
  val simulationRunner = new SimulationRunner(SimpleBlackjackEngine.simpleEngineFactory,
    entityManager => {
      (entityManager.getEntity("player").properties.getOrElse("CURRENT_TOTAL", Long.MinValue),
        entityManager.getEntity("dealer").properties.getOrElse("CURRENT_TOTAL", Long.MinValue)
        )
    }
  )
  simulationRunner.runXSimulations(3)
}
