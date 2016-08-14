package be.rubenpieters.gre.rules.instances

import be.rubenpieters.gre.entity.EntityResolver
import be.rubenpieters.gre.rules.{AbstractPropertyOverride, DefaultRule, RuleEngineParameters}

/**
  * Created by rpieters on 14/08/2016.
  */
object NullRule extends DefaultRule {
  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    Seq()
  }
}
