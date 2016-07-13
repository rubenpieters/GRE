package be.rubenpieters.gre.main

import be.rubenpieters.gre.engine.examples.simplegame.SimpleGameEngine
import be.rubenpieters.gre.simulation.SimulationRunner
import main.scala.be.rubenpieters.gre.engine.examples.blackjack.SimpleBlackjackEngine
import main.scala.be.rubenpieters.gre.engine.examples.zombie.SimpleZombieEngine

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
  val simulationRunner = new SimulationRunner(SimpleBlackjackEngine.factory(16),
    entityManager => {
      (entityManager.getEntity("player").properties.getOrElse("CURRENT_TOTAL", Long.MinValue),
        entityManager.getEntity("dealer").properties.getOrElse("CURRENT_TOTAL", Long.MinValue)
        )
    }
  )
  val results = simulationRunner.runXSimulations(100000)
  val playerWins = results.filter{x => x._2 >= 21}
  val dealerWins = results.filter{x => x._1 >= 21}
  println("player wins: " + playerWins.size)
  println("dealer wins: " + dealerWins.size)
}

object AdHocTestZombieGameSimulation extends App {
  val simulationRunner = new SimulationRunner(SimpleZombieEngine.factory(false),
    entityManager => {
      (entityManager.getEntity("village").properties.getOrElse("VILLAGERS", Long.MinValue),
        entityManager.getEntity("zombies").properties.getOrElse("ZOMBIES", Long.MinValue)
        )
    }
  )
  val results = simulationRunner.runXSimulations(1000)
  val villageWins = results.filter{x => x._2 <= 0}
  val zombiesWins = results.filter{x => x._1 <= 0}
  println("village wins: " + villageWins.size)
  println("zombies wins: " + zombiesWins.size)
}