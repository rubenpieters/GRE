package be.rubenpieters.gre.engine.examples.simplegame

import be.rubenpieters.gre.endcondition.ZeroHpEndCondition
import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.log.{ConsolePrintListener, LogListener}
import be.rubenpieters.gre.rules.{AbstractRule, SinglePropertyOperationRule}

/**
  * Created by rpieters on 15/05/2016.
  */
object SimpleGameEngine {
  def getAttackRule(defender: String): AbstractRule = {
    new SinglePropertyOperationRule(
      (hp, attacker) =>
        attacker.properties.get("ATK") match {
          case Some(atkValue) =>
            val newHpValue = hp - atkValue
            (newHpValue,
              "'" + attacker.uniqueId + "' attacks '" + defender + "' for " + atkValue +
                " (hp " + hp + " -> " + newHpValue + ")"
              )
          case None =>
            throw new IllegalStateException("No ATK value defined for entity " + attacker.uniqueId)
        },
      defender,
      "HP"
    )
  }

  def standardEnemyEntity(uniqueId: String): Entity = {
    new Entity("enemy", uniqueId, Map("HP" -> 3, "ATK" -> 1), Seq(getAttackRule("ally")))
  }

  def standardAllyEntity(uniqueId: String): Entity = {
    new Entity("ally", uniqueId, Map("HP" -> 3, "ATK" -> 1), Seq(getAttackRule("enemy")))
  }


  def simpleEngine: EngineRunner = {
    simpleEngineWithLoggers(Set(ConsolePrintListener))
  }

  def simpleEngineWithLoggers(logListeners: Set[LogListener]): EngineRunner = {
    val ally = SimpleGameEngine.standardAllyEntity("ally")
    val enemy = SimpleGameEngine.standardEnemyEntity("enemy")
    new EngineRunner(
      Seq("ally", "enemy"),
      Set(ally, enemy),
      logListeners,
      Set(ZeroHpEndCondition)
    )
  }
}
