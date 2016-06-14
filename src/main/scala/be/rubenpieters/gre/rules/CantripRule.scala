package be.rubenpieters.gre.rules
import java.util.UUID

import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 29/05/2016.
  */
class CantripRule(label: String = UUID.randomUUID().toString) extends AbstractRule(label) {
  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    val cantrippedRule = fromEntity.popRule()
    cantrippedRule.applyAndPrependRuleLabel(fromEntity, ruleEngineParameters)
  }
}
