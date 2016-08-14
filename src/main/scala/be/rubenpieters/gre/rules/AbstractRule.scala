package be.rubenpieters.gre.rules

import java.util.UUID

import be.rubenpieters.gre.entity.{ImmutableEntity, ImmutableEntityManager}

/**
  * Created by rpieters on 14/05/2016.
  */
abstract class AbstractRule {
  this: OverrideCreator with Costed with Labeled =>

  def apply(fromEntityId: String)(immutableEntityManager: ImmutableEntityManager) = {
    val propertyOverrides = createOverrides(fromEntityId, immutableEntityManager)

    val propertyOverridesPerEntity = propertyOverrides.groupBy(_.entityName)
    val updatedEntityMap = propertyOverridesPerEntity.map { case (entityName, propertyOverrideSeq) =>
      val entity = immutableEntityManager.entityMap.get(entityName).get
      (entityName,
        ImmutableEntity(
          entity.groupId,
          entity.uniqueId,
          entity.properties ++ PropertyOverride.seqPropertyOverrideToMap(propertyOverrideSeq),
          entity.ruleSet
        ))
    }
    ImmutableEntityManager(immutableEntityManager.entityMap ++ updatedEntityMap, immutableEntityManager.entityIdSequence, immutableEntityManager.nextEntityId)
  }
}

trait OverrideCreator {
  def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride]
}

trait Costed {
  def cost: Long = 1
}

trait Labeled {
  def label: String

  override def toString = {
    s"Rule[$label]"
  }
}

trait UuidLabeled extends Labeled {
  def label: String = UUID.randomUUID().toString

}

case class PropertyOverride(entityName: String, propertyName: String, newValue: Long) {

}


object PropertyOverride {
  def seqPropertyOverrideToMap(propertyOverrides: Seq[PropertyOverride]): Map[String, Long] = {
    propertyOverrides.map{ po => (po.propertyName, po.newValue)}.toMap
  }
}

abstract class DefaultRule extends AbstractRule with OverrideCreator with Costed with UuidLabeled
