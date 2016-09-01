package be.rubenpieters.gre

/**
  * Created by ruben on 29/08/2016.
  */
case class Entity(
                   id: EntityId
                   ,properties: Properties = Map()
                   ,subEntities: Map[String, Entity] = Map()
                   ,appliedEffects: Map[String, (EntityId, RunnableEffect)] = Map()
                   ,ruleEngineParameters: RuleEngineParameters
                 ) extends Identifiable with EntityResolver {
  require(! subEntities.keys.exists(_.equals(id)))

  def withNew(newProperties: Properties = properties
              ,newSubEntities: Map[String, Entity] = subEntities
              ,newAppliedEffects: Map[String, (EntityId, RunnableEffect)] = appliedEffects): Entity = {
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

  def withRunningEffects: Entity = {
    val effectsToRunning = appliedEffects.map { case (effectId, appliedEffect) =>
      appliedEffect match {
        case (actingEntity, effect: IdleEffect) => (effectId, (actingEntity, effect.toRunning))
        case (actingEntity, effect: RunningEffect) => throw new IllegalStateException()
      }
    }

    withNew(newAppliedEffects = effectsToRunning)
  }

  def applyEffects: Entity = {
    var thisWithRunningEffects = withRunningEffects

    while (thisWithRunningEffects.firstRunningEffect.isDefined) {
      val (effectId, (actingEntity, firstRunningEffect)) = thisWithRunningEffects.firstRunningEffect.get
      val currentRunningToIdleEntity = firstRunningEffect.next match {
        case Some(next) =>
          thisWithRunningEffects.withNew(newAppliedEffects = thisWithRunningEffects.appliedEffects + (effectId -> (actingEntity, next)))
        case None => thisWithRunningEffects.withNew(newAppliedEffects = thisWithRunningEffects.appliedEffects - effectId)
      }
      thisWithRunningEffects = firstRunningEffect.applyEffect(actingEntity, currentRunningToIdleEntity, ruleEngineParameters)
    }

    withNew(thisWithRunningEffects.properties, thisWithRunningEffects.subEntities, thisWithRunningEffects.appliedEffects)
  }

  def firstRunningEffect: Option[(String, (String, RunningEffect))] = {
    appliedEffects.collectFirst {
      case (effectId, (actingEntity, RunningEffect(e))) => (effectId, (actingEntity, RunningEffect(e)))
    }
  }
}

trait Identifiable {
  def id: String
}

trait EntityResolver {
  def getEntityProperty(entityId: String, propertyId: String): Long
}

