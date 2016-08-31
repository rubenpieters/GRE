package be.rubenpieters.gre

/**
  * Created by ruben on 29/08/2016.
  */
abstract class Effect {
  def next: Option[Effect]
  def effectState: EffectState

  def createOperations(actingEntity: EntityId
                      ,targetEntity: Entity
                       ,entityResolver: EntityResolver
                       ,ruleEngineParameters: RuleEngineParameters
                      ): Seq[(EntityId, Operation)] = Seq()
}

abstract class InitialEffect(effectState: InitialState) extends Effect

trait EffectState {
  def nextState: Option[EffectState]
}

case class InitialState(nextState: Some[EffectState]) extends EffectState

case class EffectRunning(counter: Int) extends EffectState {
  require(counter > 0)

  val nextState = counter match {
    case 1 => Some(EffectEnding)
    case _ => Some(EffectRunning(counter - 1))
  }
}

case object EffectEnding extends EffectState {
  val nextState = None
}
