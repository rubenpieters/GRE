package be.rubenpieters.gre.rules

import java.util.UUID

import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 14/05/2016.
  */
abstract class AbstractRule(val label: String = UUID.randomUUID().toString) {

  def cost: Long

  def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String

  def applyAndPrependRuleLabel(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    prependRuleLabel(apply(fromEntity, ruleEngineParameters))
  }

  def prependRuleLabel(line: String): String = {
    this.getClass.getSimpleName + "[" + label + "]: " + line
  }
}
