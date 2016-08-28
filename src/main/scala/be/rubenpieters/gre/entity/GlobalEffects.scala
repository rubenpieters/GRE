package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.AbstractRule

/**
  * Created by rpieters on 28/08/2016.
  */
class GlobalEffects(val effects: List[Effect] = List[Effect]()) extends GlobalEffectEntityLike {
  val uniqueId = "___GLOBAL_EFFECTS"
  val properties = Map[String, Long]()
}

trait EffectState

case class EffectRunning(counter: Int) extends EffectState {
  def next: EffectState = {
    if (counter > 1) {
      EffectRunning(counter - 1)
    } else {
      EffectFinished
    }
  }
}
object EffectFinished extends EffectState