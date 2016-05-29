package be.rubenpieters.gre.rules
import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 29/05/2016.
  */
object CantripRule extends AbstractRule {
  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    val cantrippedRule = fromEntity.popRule()
    cantrippedRule.apply(fromEntity, ruleEngineParameters)
  }
}
