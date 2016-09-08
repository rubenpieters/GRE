package be.rubenpieters.gre

/**
  * Created by ruben on 8/09/2016.
  */
trait Scope { self: RecursiveEntity with EntityResolver with Identifiable =>
  def ruleEngineParameters: RuleEngineParameters

  def applyRule(rule: AbstractRule, actingEntity: EntityId): RecursiveEntity = {
    val operations = rule.createOperations(actingEntity, this, ruleEngineParameters)
    val updatedEntities = operations.groupBy(_._1).map { case (target, operationSeq) =>
      val targetEntity = getEntity(target)
      val newEntity = operationSeq
        .map(_._2)
        .foldLeft(targetEntity)((accEntity, currentOp) => currentOp.applyOperation(accEntity))
      (target, newEntity)
    }

    // if this is inside the updated entities, grab it; if not just use this
    val updatedThis = updatedEntities.getOrElse(id, this)
    // replace the subentites with the updatedentities, remove this (or the updated this) since we already grabbed it
    withUpdatedSubEntities(subEntities = subEntities ++ (updatedEntities - id))
  }

}
