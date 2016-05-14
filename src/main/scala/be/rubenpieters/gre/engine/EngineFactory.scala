package be.rubenpieters.gre.engine

import be.rubenpieters.gre.entity.EntityFactory
import be.rubenpieters.gre.log.ConsolePrintListener

/**
  * Created by rpieters on 14/05/2016.
  */
object EngineFactory {
  def simpleEngine: EngineRunner = {
    val ally = EntityFactory.standardAllyEntity("ally")
    val enemy = EntityFactory.standardEnemyEntity("enemy")
    new EngineRunner(
      Seq("ally", "enemy"),
      Set(ally, enemy),
      Set(ConsolePrintListener)
    )
  }
}
