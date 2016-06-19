package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.AbstractRule

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

  def goToLabel(label: String) = {
    val ruleIndex = ruleSet.indexWhere{rule => rule.label.equals(label)}
    if (ruleIndex == -1) {
      throw new IllegalStateException("Entity " + uniqueId + " does not have a rule with label " + label)
    }
    ruleSet = baseRuleSet.drop(ruleIndex)
  }

  def updateRuleSet() = {
    ruleSet = ruleSet.drop(1)
    if (ruleSet.isEmpty) {
      ruleSet = baseRuleSet
    }
  }
}

