package be.rubenpieters.gre.endcondition

import be.rubenpieters.gre.engine.EngineRunner

/**
  * Created by rpieters on 14/05/2016.
  */
abstract class EndCondition {
  def checkCondition(engineRunner: EngineRunner): Option[String]
}
