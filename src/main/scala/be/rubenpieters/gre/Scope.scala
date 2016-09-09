package be.rubenpieters.gre

/**
  * Created by ruben on 8/09/2016.
  */
trait Scope { self: RecursiveEntity with EntityResolver with Identifiable =>
  def ruleEngineParameters: RuleEngineParameters

  def applyRule(rule: AbstractRule, actingEntity: EntityId): RecursiveEntity = {
    val operations = rule.createOperations(actingEntity, this, ruleEngineParameters)
    val updatedEntities = operations.groupBy(_._1).map { case (target, operationSeq) =>
      val targetEntity = getEntityUnsafe(target)
      val newEntity = operationSeq
        .map(_._2)
        .foldLeft(targetEntity)((accEntity, currentOp) => currentOp.applyOperation(accEntity))
      (target, newEntity)
    }

    // if this is inside the updated entities, grab it; if not just use this
    // TODO: get rid of the cast
    val updatedThis = updatedEntities.getOrElse(id, this).asInstanceOf[RecursiveEntity]
    // replace the subentites with the updatedentities, remove this (or the updated this) since we already grabbed it
    updatedThis.withUpdatedSubEntities(subEntities = subEntities ++ (updatedEntities - id))
  }

  def advance: RecursiveEntity = {
    // TODO: move the advance logic code to somewhere more generic?
    val nextEntity = entitiesByProperty("INITIATIVE").head
    val (nextEntityUpdated, rule) = nextEntity.popRule
    val scopeUpdated = nextEntityUpdated.id match {
      case updatedId if updatedId.equals(id) => nextEntityUpdated
      case _ => withUpdatedSubEntities(subEntities = subEntities + (nextEntityUpdated.id -> nextEntityUpdated))
    }
    // TODO: get rid of the cast
    scopeUpdated.asInstanceOf[Scope].applyRule(rule, nextEntityUpdated.id)
  }
}
