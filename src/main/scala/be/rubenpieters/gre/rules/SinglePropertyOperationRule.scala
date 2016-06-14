package be.rubenpieters.gre.rules
import java.util.UUID

import be.rubenpieters.gre.entity.Entity

/**
  * Created by rpieters on 14/05/2016.
  */
class SinglePropertyOperationRule(
                                   label: String = UUID.randomUUID().toString,
                                   operation: (Long, Entity, RuleEngineParameters) => (Long, String),
                                   entityName: String,
                                   propertyName: String
                                 ) extends AbstractRule(label) {
  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    val entityManager = ruleEngineParameters.entityManager

    val toEntity = entityManager.getEntity(entityName)
    toEntity.properties.get(propertyName) match {
      case Some(propertyValue) =>
        val (newPropertyValue, logLine) = operation.apply(propertyValue, fromEntity, ruleEngineParameters)
        toEntity.properties = toEntity.properties + (propertyName -> newPropertyValue)
        logLine
      case None =>
        throw new IllegalStateException("Unable to modify undefined property " + propertyName + " on entity: " + toEntity.uniqueId)
    }
  }
}
