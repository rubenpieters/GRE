package be.rubenpieters.gre

/**
  * Created by ruben on 1/09/2016.
  */
trait RuleAdvanceStrategy {
  def advance(entity: Entity): Entity
  def rule: AbstractRule
}

case class CyclicRuleStrategy(ruleSeq: Seq[AbstractRule], pointer: Int = 0) extends RuleAdvanceStrategy {
  require(pointer >= 0)
  require(pointer < ruleSeq.size)

  override val rule: AbstractRule = ruleSeq(pointer)

  lazy val nextPointer = pointer + 1
  lazy val nextRule: RuleAdvanceStrategy = nextPointer >= ruleSeq.size match {
    case true => CyclicRuleStrategy(ruleSeq, 0)
    case false => CyclicRuleStrategy(ruleSeq, nextPointer)
  }

  override def advance(entity: Entity): Entity = {
    entity.copy(ruleAdvanceStrategy = nextRule)
  }
}

// TODO: optimize this, dont need to keep the complete shuffled sequence in memory

case class ShuffledCyclicRuleWithRepresentationStrategy(
                                                         ruleSeq: Seq[(AbstractRule, Int)]
                                                         ,fullRules: Seq[AbstractRule]
                                                         ,ruleEngineParameters: RuleEngineParameters
                                                         ,pointer: Int = 0
                                                       )
  extends RuleAdvanceStrategy {
  require(pointer >= 0)
  require(pointer < fullRules.size)

  override def rule: AbstractRule = fullRules(pointer)

  lazy val nextPointer = pointer + 1
  lazy val nextRule: RuleAdvanceStrategy = nextPointer >= fullRules.size match {
    case true => ShuffledCyclicRuleWithRepresentationStrategy.fromRuleSeq(ruleSeq, ruleEngineParameters)
    case false => ShuffledCyclicRuleWithRepresentationStrategy(ruleSeq, fullRules, ruleEngineParameters, nextPointer)
  }

  override def advance(entity: Entity): Entity = {
    entity.copy(ruleAdvanceStrategy = nextRule)
  }

  override def toString = {
    s"Pointer: $pointer, Full Rules: ${fullRules.map(_.label)}"
  }
}

object ShuffledCyclicRuleWithRepresentationStrategy {
  def fromRuleSeq(ruleSeq: Seq[(AbstractRule, Int)], ruleEngineParameters: RuleEngineParameters) = {
    val reshuffledRules = ruleEngineParameters.rng.shuffle(ruleSeq.flatMap{ case (rule, reprAmt) => Seq.fill(reprAmt)(rule)})
    ShuffledCyclicRuleWithRepresentationStrategy(ruleSeq, reshuffledRules, ruleEngineParameters, 0)
  }
}

case class WorldWithInitiativeRuleStrategy(ruleStrategyEntity: Entity) extends RuleAdvanceStrategy {
  lazy val currentActiveEntity = ruleStrategyEntity.entitiesByProperty("INITIATIVE").head

  override def advance(entity: Entity): Entity = ???

  override def rule: AbstractRule = ???
}