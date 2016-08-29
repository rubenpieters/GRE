package be.rubenpieters

/**
  * Created by ruben on 29/08/2016.
  */
case class Entity(
                  id: EntityId
                  ,properties: Properties = Map()
                  ,subEntities: Map[String, Entity] = Map()
                 ) extends Identifiable with EntityResolver {
  require(! subEntities.keys.exists(_.equals(id)))

  def getProperty(propertyId: String): Long = {
    properties.get(propertyId) match {
      case Some(p) => p
      case None => throw new IllegalArgumentException(s"Entity $id does not contain property $propertyId")
    }
  }

  def getEntity(findId: String): Entity = {
    id.equals(findId) match {
      case true => this
      case false => subEntities.get(findId) match {
        case Some(e) => e
        case None => throw new IllegalArgumentException(s"Entity $id does not contain an entity with id $findId")
      }
    }
  }

  def getEntityProperty(entityId: EntityId, propertyId: String): Long = {
    getEntity(entityId).getProperty(propertyId)
  }

  def applyOperation(operation: Operation): Entity = {
    Entity(
      id
      ,operation.newProperties(properties)
      ,subEntities
    )
  }

  def applyRule(rule: AbstractRule, actingEntity: EntityId, ruleEngineParameters: RuleEngineParameters): Entity = {
    val operations = rule.createOperations(actingEntity, this, ruleEngineParameters)

    val effects = rule.createEffects(actingEntity, this, ruleEngineParameters)

    this
  }
}

trait Identifiable {
  def id: String
}

trait EntityResolver {
  def getEntityProperty(entityId: String, propertyId: String): Long
}

