package be.rubenpieters

/**
  * Created by ruben on 29/08/2016.
  */
abstract class AbstractRule {
  def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = Seq()
  def createEffects(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Effect)] = Seq()
}
