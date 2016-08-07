package be.rubenpieters.gre.rules.meta

import java.util.UUID

import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.rules.{AbstractRule, RuleEngineParameters}

/**
  * Created by rpieters on 7/08/2016.
  */
class ComposedRule(label: String = UUID.randomUUID().toString, ruleSeq: Seq[AbstractRule]) extends AbstractRule(label) {
  override def cost: Long = ruleSeq.map(_.cost).sum

  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    ruleSeq.map(_.applyAndPrependRuleLabel(fromEntity, ruleEngineParameters)).mkString("[", ", ", "]")
  }
}
