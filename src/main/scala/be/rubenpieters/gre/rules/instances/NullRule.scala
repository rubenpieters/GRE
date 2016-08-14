package be.rubenpieters.gre.rules.instances

import be.rubenpieters.gre.entity.ImmutableEntityManager
import be.rubenpieters.gre.rules.{DefaultRule, PropertyOverride}

/**
  * Created by rpieters on 14/08/2016.
  */
object NullRule extends DefaultRule {
  override def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride] = {
    Seq()
  }
}
