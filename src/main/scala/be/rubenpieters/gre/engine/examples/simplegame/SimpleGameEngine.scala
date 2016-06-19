package be.rubenpieters.gre.engine.examples.simplegame

import java.util.UUID

import be.rubenpieters.gre.endcondition.ZeroHpEndCondition
import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.log.{ConsolePrintListener, LogListener}
import be.rubenpieters.gre.rules._

/**
  * Created by rpieters on 15/05/2016.
  */
object SimpleGameEngine {
  def getAttackRule(defender: String, label: String): AbstractRule = {
    new SinglePropertyOperationRule(
      label,
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

  def getRandomAttackRule(defender: String, label: String): AbstractRule = {
    new SinglePropertyOperationRule(
      label,
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

  def getHealRule(target: String, label: String): AbstractRule = {
    new SinglePropertyOperationRule(
      label,
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

  def getHealIfBelowHealthThresholdRule(target: String, threshold: Float, label: String): AbstractRule = {
    new IfConditionRule(
      label,
      (fromEntity, params) => {
        val targetEntity = params.entityManager.getEntity(target)
        val hp = targetEntity.properties.get("HP").get
        val maxHp = targetEntity.properties.get("MAX_HP").get
        (hp.toFloat / maxHp.toFloat) < threshold
      },
      getHealRule(target, UUID.randomUUID.toString),
      //new CantripRule(UUID.randomUUID.toString)
      new JumpToLabelRule("HEAL_ELSE_JMP", "LBL_AL_ATK")
    )
  }

  def standardEnemyEntity(uniqueId: String): Entity = {
    new Entity("enemy", uniqueId,
      Map("HP" -> 5, "MAX_HP" -> 5, "ATK_MIN" -> 45, "ATK_MAX" -> 80, "HEAL_VALUE" -> 1),
      Seq(getRandomAttackRule("ally", "LBL_EN_ATK"), getHealRule("enemy", "LBL_EN_HEAL")))
  }

  def standardAllyEntity(uniqueId: String): Entity = {
    new Entity("ally", uniqueId,
      Map("HP" -> 100, "MAX_HP" -> 100, "ATK" -> 2, "HEAL_VALUE" -> 50),
      Seq(getAttackRule("enemy", "LBL_AL_ATK"), getHealIfBelowHealthThresholdRule("ally", 0.5f, "LBL_AL_HEAL")))
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
