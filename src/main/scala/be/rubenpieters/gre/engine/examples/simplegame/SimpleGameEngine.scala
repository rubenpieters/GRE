package be.rubenpieters.gre.engine.examples.simplegame

import be.rubenpieters.gre.endcondition.ZeroHpEndCondition
import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.log.{ConsolePrintListener, LogListener}
import be.rubenpieters.gre.rules.{AbstractRule, RuleEngineParameters, SinglePropertyOperationRule}

/**
  * Created by rpieters on 15/05/2016.
  */
object SimpleGameEngine {
  def getAttackRule(defender: String): AbstractRule = {
    new SinglePropertyOperationRule(
      (hp, attacker, _) =>
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

  def getRandomAttackRule(defender: String): AbstractRule = {
    new SinglePropertyOperationRule(
      (hp, attacker, ruleEngineParameters) =>
      {
        val rng = ruleEngineParameters.rng
        val minAtk = attacker.properties.get("ATK_MIN").get
        val maxAtk = attacker.properties.get("ATK_MAX").get
        val atkValue = rng.nextInt(maxAtk.toInt - minAtk.toInt + 1) + minAtk
        val newHpValue = hp - atkValue
        (newHpValue,
          "'" + attacker.uniqueId + "' attacks '" + defender + "' for " + atkValue +
            " (hp " + hp + " -> " + newHpValue + ")"
          )
      },
      defender,
      "HP"
    )
  }

  def getHealRule(target: String): AbstractRule = {
    new SinglePropertyOperationRule(
      (hp, healer, ruleEngineParameters) => {
        val targetEntity = ruleEngineParameters.entityManager.getEntity(target)
        val healValue = healer.properties.get("HEAL_VALUE").get
        val maxHpValue = targetEntity.properties.get("MAX_HP").get
        val newHpValue = hp + healValue
        if (newHpValue > maxHpValue) {
          (maxHpValue,
            "'" + healer.uniqueId + "' heals '" + target + "' for " + (maxHpValue - hp) +
              ", " + (newHpValue - maxHpValue) + " hp overheal (hp " + hp + " -> " + maxHpValue + ")"
            )
        } else {
          (newHpValue,
            "'" + healer.uniqueId + "' heals '" + target + "' for " + healValue +
              " (hp " + hp + " -> " + newHpValue + ")"
            )
        }
      },
      target,
      "HP"
    )
  }

  def standardEnemyEntity(uniqueId: String): Entity = {
    new Entity("enemy", uniqueId,
      Map("HP" -> 5, "MAX_HP" -> 5, "ATK_MIN" -> 5, "ATK_MAX" -> 20, "HEAL_VALUE" -> 1),
      Seq(getRandomAttackRule("ally"), getHealRule("enemy")))
  }

  def standardAllyEntity(uniqueId: String): Entity = {
    new Entity("ally", uniqueId,
      Map("HP" -> 100, "MAX_HP" -> 100, "ATK" -> 2, "HEAL_VALUE" -> 5),
      Seq(getAttackRule("enemy"), getHealRule("ally")))
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
      Set(ZeroHpEndCondition),
      1L
    )
  }
}
