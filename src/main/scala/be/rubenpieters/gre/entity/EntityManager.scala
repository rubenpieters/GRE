package be.rubenpieters.gre.entity

/**
  * Created by rpieters on 14/05/2016.
  */
class EntityManager {
  var entityMap: Map[String, Entity] = Map()

  def registerEntities(entities: Set[Entity]) = {
    entities.foreach(registerEntity(_))
  }

  def registerEntity(entity: Entity) = {
    entityMap = entityMap + (entity.uniqueId -> entity)
  }

  def getEntity(id: String): Entity = {
    entityMap.getOrElse(id, NullEntity.instance)
  }
}
