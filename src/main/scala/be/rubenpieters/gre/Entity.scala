package be.rubenpieters.gre

/**
  * Created by ruben on 29/08/2016.
  */
case class Entity(
                   id: EntityId
                   ,properties: Properties = Map()
                   ,subEntities: Map[String, Entity] = Map()
                   ,appliedEffects: Seq[(String, Effect)] = Seq()
                   ,ruleEngineParameters: RuleEngineParameters
                 ) extends Identifiable with EntityResolver {
  require(! subEntities.keys.exists(_.equals(id)))

  def withNew(newProperties: Properties = properties
              ,newSubEntities: Map[String, Entity] = subEntities
              ,newAppliedEffects: Seq[(String, Effect)] = appliedEffects): Entity = {
    Entity(
      id
      ,newProperties
      ,newSubEntities
      ,newAppliedEffects
      ,ruleEngineParameters
    )
  }

  def getProperty(propertyId: String): Long = {
    properties.get(propertyId) match {
      case Some(p) => p
      case None => throw new IllegalArgumentException(s"Entity $id does not contain property $propertyId")
    }
  }

  def getEntity(findId: String): Entity = {
    id.equals(findId) match {
      case true => this
      case false =>
        // NOTE: this sort of assumes that entity ids are globally unique, or at least within all possible scopes
        val firstFoundEntity = subEntities.collectFirst { case (entityId, entity) =>
          entity.getEntity(entityId)
        }
        firstFoundEntity match {
          case Some(e) => e
          case None => throw new IllegalArgumentException(s"Entity or its subEntities $id does not contain an entity with id $findId")
        }
    }
  }

  def getEntityProperty(entityId: EntityId, propertyId: String): Long = {
    getEntity(entityId).getProperty(propertyId)
  }

  def applyEffects: Entity = {
    // zip the rules with index and advance via index, attach new rules at the end of the seq
    val newThis = appliedEffects.foldLeft(this) { (accEntity, appliedEffect) =>
      val actingEntityId = appliedEffect._1
      val effect = appliedEffect._2

      //applyRule(effect.effectRule, actingEntityId)
      this
    }

    withNew(newThis.properties, newThis.subEntities, newThis.appliedEffects)
  }

  def applyRule(rule: AbstractRule, actingEntity: EntityId): Entity = {
    val operations = rule.createOperations(actingEntity, this, ruleEngineParameters)
    operations.groupBy(_._1).map { case (target, operationSeq) =>
      val targetEntity = getEntity(target)
      val newEntity = operationSeq
        .map(_._2)
        .foldLeft(targetEntity)((accEntity, currentOp) => currentOp.applyOperation(accEntity))
      (target, newEntity)
    }

    this
  }
}

trait Identifiable {
  def id: String
}

trait EntityResolver {
  def getEntityProperty(entityId: String, propertyId: String): Long
}

