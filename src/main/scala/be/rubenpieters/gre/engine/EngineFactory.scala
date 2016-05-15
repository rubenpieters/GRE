package be.rubenpieters.gre.engine

import be.rubenpieters.gre.endcondition.ZeroHpEndCondition
import be.rubenpieters.gre.entity.EntityFactory
import be.rubenpieters.gre.log.{ConsolePrintListener, LogListener}

/**
  * Created by rpieters on 14/05/2016.
  */
object EngineFactory {
  def simpleEngine: EngineRunner = {
    simpleEngineWithLoggers(Set(ConsolePrintListener))
  }

  def simpleEngineWithLoggers(logListeners: Set[LogListener]): EngineRunner = {
    val ally = EntityFactory.standardAllyEntity("ally")
    val enemy = EntityFactory.standardEnemyEntity("enemy")
    new EngineRunner(
      Seq("ally", "enemy"),
      Set(ally, enemy),
      logListeners,
      Set(ZeroHpEndCondition)
    )
  }
}
