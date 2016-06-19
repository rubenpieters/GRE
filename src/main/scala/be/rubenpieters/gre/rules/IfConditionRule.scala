package be.rubenpieters.gre.rules
import java.util.UUID

import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 29/05/2016.
  */
class IfConditionRule(
                       label: String = UUID.randomUUID().toString,
                       condition: (Entity, RuleEngineParameters) => Boolean,
                       trueRule: AbstractRule,
                       falseRule: AbstractRule
                     ) extends AbstractRule(label) {
  override def cost: Long = 2

  override def apply(fromEntity: Entity,
                     ruleEngineParameters: RuleEngineParameters
                    ): String = {
    if (condition.apply(fromEntity, ruleEngineParameters)) {
      trueRule.applyAndPrependRuleLabel(fromEntity, ruleEngineParameters)
    } else {
      falseRule.applyAndPrependRuleLabel(fromEntity, ruleEngineParameters)
    }
  }
}
