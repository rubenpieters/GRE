package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.RuleSet
import be.rubenpieters.gre.rules.instances.NullRule

/**
  * Created by rpieters on 14/05/2016.
  */
object NullEntity {
  val instance = new ImmutableEntity("___RESERVED___NULL", "___RESERVED___NULL", Map(), RuleSet.init(Seq(NullRule)))
}
