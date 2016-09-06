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
    entity.withNew(newRuleAdvanceStrategy = nextRule)
  }
}

case class WorldWithInitiativeRuleStrategy(ruleStrategyEntity: Entity) extends RuleAdvanceStrategy {
  lazy val currentActiveEntity = ruleStrategyEntity.entitiesByProperty("INITIATIVE").head
  
  override def advance(entity: Entity): Entity = ???

  override def rule: AbstractRule = ???
}