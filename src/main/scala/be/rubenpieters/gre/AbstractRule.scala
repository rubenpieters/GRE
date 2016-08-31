package be.rubenpieters.gre

/**
  * Created by ruben on 29/08/2016.
  */
abstract class AbstractRule {
  def createOperations(actingEntity: EntityId
                       ,entityResolver: EntityResolver
                       ,ruleEngineParameters: RuleEngineParameters
                      ): Seq[(EntityId, Operation)] = Seq()
}
