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

case class CyclicRuleWithRepresentationStrategy(ruleSeq: Seq[(AbstractRule, Int)], ruleEngineParameters: RuleEngineParameters, pointer: Int = 0) extends RuleAdvanceStrategy {
  val fullRules = ruleEngineParameters.rng.shuffle(ruleSeq.flatMap{ case (rule, reprAmt) => Seq.fill(reprAmt)(rule)})
  require(pointer >= 0)
  require(pointer < fullRules.size)

  override def rule: AbstractRule = fullRules(pointer)

  lazy val nextPointer = pointer + 1
  lazy val nextRule: RuleAdvanceStrategy = nextPointer >= fullRules.size match {
    case true => CyclicRuleWithRepresentationStrategy(ruleSeq, ruleEngineParameters)
    case false => CyclicRuleWithRepresentationStrategy(ruleSeq, ruleEngineParameters, nextPointer)
  }

  override def advance(entity: Entity): Entity = {
    entity.copy(ruleAdvanceStrategy = nextRule)
  }

  override def toString = {
    s"Pointer: $pointer, Full Rules: ${fullRules.map(_.label)}"
  }
}

case class WorldWithInitiativeRuleStrategy(ruleStrategyEntity: Entity) extends RuleAdvanceStrategy {
  lazy val currentActiveEntity = ruleStrategyEntity.entitiesByProperty("INITIATIVE").head

  override def advance(entity: Entity): Entity = ???

  override def rule: AbstractRule = ???
}