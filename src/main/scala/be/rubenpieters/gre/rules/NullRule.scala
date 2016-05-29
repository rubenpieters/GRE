package be.rubenpieters.gre.rules
import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 14/05/2016.
  */
class NullRule extends AbstractRule {
  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    "NullRule: nothing happens"
  }
}
