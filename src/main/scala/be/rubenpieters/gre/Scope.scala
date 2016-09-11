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

    // if all entities are out of initiative, increase their initiative
    val updatedThis = if (subEntities.forall { case (_, e) => e.getProperty("INITIATIVE") <= 0}) {
      val updatedEntities = subEntities.mapValues { e => PlusPropertyOverride(
        this, e.id, "INITIATIVE", getEntityPropertySafe(e.id, "IN_INC").getOrElse(0)
      ).applyOperation(e)
      }
      withUpdatedSubEntities(subEntities = updatedEntities)
    } else {
      this
    }


    val nextEntity = updatedThis.entitiesByProperty("INITIATIVE").head
    val (nextEntityUpdated, rule) = nextEntity.popRule
    val nextEntityUpdatedMinusInit = MinusPropertyOverride(
      updatedThis.asInstanceOf[EntityResolver], nextEntityUpdated.id, "INITIATIVE", getEntityPropertySafe(nextEntityUpdated.id, "IN_DEC").getOrElse(0)
    ).applyOperation(nextEntityUpdated)
    val scopeUpdated = nextEntityUpdatedMinusInit.id match {
      case updatedId if updatedId.equals(id) => nextEntityUpdatedMinusInit
      case _ => updatedThis.withUpdatedSubEntities(subEntities = updatedThis.subEntities + (nextEntityUpdatedMinusInit.id -> nextEntityUpdatedMinusInit))
    }
    // TODO: get rid of the cast
    scopeUpdated.asInstanceOf[Scope].applyRule(rule, nextEntityUpdatedMinusInit.id)
  }
}
