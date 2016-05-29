package be.rubenpieters.gre.rules
import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 29/05/2016.
  */
class IfConditionRule(condition: (Entity, RuleEngineParameters) => Boolean,
                      trueRule: AbstractRule,
                      falseRule: AbstractRule
                     ) extends AbstractRule {
  override def apply(fromEntity: Entity,
                     ruleEngineParameters: RuleEngineParameters
                    ): String = {
    if (condition.apply(fromEntity, ruleEngineParameters)) {
      trueRule.apply(fromEntity, ruleEngineParameters)
    } else {
      falseRule.apply(fromEntity, ruleEngineParameters)
    }
  }
}
