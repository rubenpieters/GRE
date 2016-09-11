package be.rubenpieters.gre

import java.util.UUID

/**
  * Created by ruben on 29/08/2016.
  */
abstract class AbstractRule extends Labeled {
  def createOperations(actingEntity: EntityId
                       ,entityResolver: EntityResolver
                       ,ruleEngineParameters: RuleEngineParameters
                      ): Seq[(EntityId, Operation)] = Seq()

  override def toString = label
}

trait Labeled {
  def label: String
}

trait UUIDLabeled extends Labeled {
  def label = UUID.randomUUID().toString
}