package be.rubenpieters.gre

/**
  * Created by ruben on 29/08/2016.
  */
trait RunnableEffect {
  def effect: Effect

  def applyEffect(actingEntity: EntityId, targetEntity: Entity, ruleEngineParameters: RuleEngineParameters) = {
    val operations = effect.createOperations(actingEntity, targetEntity, targetEntity, ruleEngineParameters)
    operations.foldLeft(targetEntity)((accEntity, currentOp) => currentOp.applyOperation(accEntity))
  }
}

case class RunningEffect(effect: Effect) extends RunnableEffect {
  lazy val next: Option[IdleEffect] = effect.next.map { nextEffect =>
    IdleEffect(nextEffect)
  }
}

case class IdleEffect(effect: Effect) extends RunnableEffect {
  lazy val toRunning: RunningEffect = RunningEffect(effect)
}

abstract class Effect(effectState: EffectState) {
  def next: Option[Effect] = effectState.nextState.map { state =>
    createWithNewState(state)
  }
  def createWithNewState(effectState: EffectState): Effect
  var toApply: Boolean = false

  def createOperations(actingEntity: EntityId
                       ,targetEntity: Entity
                       ,entityResolver: EntityResolver
                       ,ruleEngineParameters: RuleEngineParameters
                      ): Seq[Operation]
}


trait EffectState {
  def nextState: Option[EffectState]
}

case class EffectRunning(counter: Int) extends EffectState {
  require(counter > 0)

  override val nextState: Option[EffectState] = counter match {
    case 1 => Some(EffectEnding)
    case _ => Some(EffectRunning(counter - 1))
  }
}

case object EffectEnding extends EffectState {
  override val nextState: Option[EffectState] = None
}
