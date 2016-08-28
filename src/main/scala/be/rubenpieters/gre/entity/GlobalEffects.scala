package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.AbstractRule

/**
  * Created by rpieters on 28/08/2016.
  */
class GlobalEffects extends GlobalEffectEntityLike {
  val uniqueId = "___GLOBAL_EFFECTS"
  val properties = Map[String, Long]()
  val effects = List[Effect]()
}
