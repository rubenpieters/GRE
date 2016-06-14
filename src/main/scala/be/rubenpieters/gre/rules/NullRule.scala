package be.rubenpieters.gre.rules
import java.util.UUID

import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 14/05/2016.
  */
class NullRule(label: String = UUID.randomUUID().toString) extends AbstractRule(label) {
  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    "Nothing happens"
  }
}
