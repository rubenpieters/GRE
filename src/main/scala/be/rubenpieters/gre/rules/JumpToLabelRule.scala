package be.rubenpieters.gre.rules

import java.util.UUID

import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 18/06/2016.
  */
class JumpToLabelRule(label: String = UUID.randomUUID().toString, toLabel: String) extends AbstractRule(label) {
  override def cost: Long = 2

  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    if (label.equals(toLabel)) {
      throw new IllegalStateException("illegal jump rule")
    }
    fromEntity.goToLabel(toLabel)
    val nextRule = fromEntity.popRule()
    nextRule.applyAndPrependRuleLabel(fromEntity, ruleEngineParameters)
  }
}
