package be.rubenpieters.gre.engine

import be.rubenpieters.gre.endcondition.{EndCondition, ZeroHpEndCondition}
import be.rubenpieters.gre.engine.examples.simplegame.SimpleGameEngine
import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.log.{ConsolePrintListener, LogListener}

/**
  * Created by rpieters on 14/05/2016.
  */
// instead of working with an entitySet creation function, could rewrite code to have immutable entities?
class EngineFactory(entityOrder: Seq[String],
                    entityCreation: () => Set[Entity],
                    logListeners: Set[LogListener],
                    endConditions: Set[EndCondition]) {


  def newEngineRunner(seed: Long): EngineRunner = {
    new EngineRunner(
      entityOrder,
      entityCreation.apply(),
      logListeners,
      endConditions,
      seed
    )
  }
}
