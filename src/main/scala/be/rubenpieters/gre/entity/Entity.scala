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

