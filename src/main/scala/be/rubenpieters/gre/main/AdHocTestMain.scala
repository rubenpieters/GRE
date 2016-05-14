package be.rubenpieters.gre.main

import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.entity.EntityFactory

/**
  * Created by rpieters on 14/05/2016.
  */
object AdHocTestMain extends App {
  val ally = EntityFactory.standardAllyEntity("ally")
  val enemy = EntityFactory.standardEnemyEntity("enemy")
  val engineRunner = new EngineRunner(Seq("ally", "enemy"), Set(ally, enemy))

  engineRunner.runStep()
  println("ally: " + ally.properties)
  println("enemy: " + enemy.properties)

  engineRunner.runStep()
  println("ally: " + ally.properties)
  println("enemy: " + enemy.properties)

}
