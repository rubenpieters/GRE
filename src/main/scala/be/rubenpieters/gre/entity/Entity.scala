package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.{SinglePropertyOperationRule, AbstractRule}

/**
  * Created by rpieters on 14/05/2016.
  */
class Entity(
              val groupId: String,
              val uniqueId: String,
              var properties: Map[String, Long],
              var ruleSet: Seq[AbstractRule]
            ) {
  val baseRuleSet = ruleSet

  def popRule(): AbstractRule = {
    val poppedRule = ruleSet.head
    updateRuleSet()
    poppedRule
  }

  def updateRuleSet() = {
    ruleSet = ruleSet.drop(1)
    if (ruleSet.isEmpty) {
      ruleSet = baseRuleSet
    }
  }
}

object EntityFactory {
  def getAttackRule(defender: String): AbstractRule = {
    new SinglePropertyOperationRule(
      (hp, attacker) =>
        attacker.properties.get("ATK") match {
          case Some(atkValue) => hp - atkValue
          case None => throw new IllegalStateException("No ATK value defined for entity " + attacker.uniqueId)
        },
      defender,
      "HP"
    )
  }

  def standardEnemyEntity(uniqueId: String): Entity = {
    new Entity("enemy", uniqueId, Map("HP" -> 10, "ATK" -> 1), Seq(getAttackRule("ally")))
  }

  def standardAllyEntity(uniqueId: String): Entity = {
    new Entity("ally", uniqueId, Map("HP" -> 10, "ATK" -> 1), Seq(getAttackRule("enemy")))
  }
}