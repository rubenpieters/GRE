package be.rubenpieters.gre

/**
  * Created by ruben on 29/08/2016.
  */
case class Effect(effectState: EffectState) {
  def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = Seq()
  def createEffects(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Effect)] = Seq()
}

trait EffectState {
  def nextState: Option[EffectState]
}
case class EffectRunning(counter: Int) {
  require(counter > 0)

  val nextState = counter match {
    case 1 => Some(EffectEnding)
    case _ => Some(EffectRunning(counter - 1))
  }
}

object EffectEnding {
  val nextState = None
}
