package be.rubenpieters.gre.rules
import be.rubenpieters.gre.entity.{Entity, EntityManager}

import scala.util.Random

/**
  * Created by rpieters on 14/05/2016.
  */
class SinglePropertyOperationRule(
                                   operation: (Long, Entity, Random) => (Long, String),
                                   entityName: String,
                                   propertyName: String
                                 ) extends AbstractRule {
  override def apply(fromEntity: Entity, entityManager: EntityManager, rng: Random): String = {
    val toEntity = entityManager.getEntity(entityName)
    toEntity.properties.get(propertyName) match {
      case Some(propertyValue) =>
        val (newPropertyValue, logLine) = operation.apply(propertyValue, fromEntity, rng)
        toEntity.properties = toEntity.properties + (propertyName -> newPropertyValue)
        logLine
      case None =>
        throw new IllegalStateException("Unable to modify undefined property " + propertyName + " on entity: " + toEntity.uniqueId)
    }
  }
}
