package be.rubenpieters.gre.rules

import java.util.UUID

import be.rubenpieters.gre.entity.{EntityResolver, ImmutableEntity, ImmutableEntityManager}

/**
  * Created by rpieters on 14/05/2016.
  */
abstract class AbstractRule {
  this: OverrideCreator with Costed with Labeled =>

  def apply(fromEntityName: String)(immutableEntityManager: ImmutableEntityManager, ruleEngineParameters: RuleEngineParameters) = {
    val propertyOverrides = createOverrides(fromEntityName, immutableEntityManager, ruleEngineParameters)
    propertyOverrides.foreach(println)

    // update properties of all entities
    val propertyOverridesPerEntity = propertyOverrides.groupBy(_.entityName)
    val updatedEntityMap =
      // old entity map
      immutableEntityManager.entityMap ++
      // overridden by the updated entities
      propertyOverridesPerEntity.map { case (entityName, propertyOverrideSeq) =>
      val entity = immutableEntityManager.entityMap.get(entityName).get
      (entityName,
        ImmutableEntity(
          entity.groupId,
          entity.uniqueId,
          entity.properties ++ AbstractPropertyOverride.seqPropertyOverrideToMap(propertyOverrideSeq),
          entity.ruleSet
        ))
    }
    // update rule counter of fromEntity
    val fromEntity = updatedEntityMap(fromEntityName)
    val fromEntityWithIncrRuleCounter = Map(fromEntityName -> fromEntity.withIncrRuleCounter)

    ImmutableEntityManager(updatedEntityMap ++ fromEntityWithIncrRuleCounter,
      immutableEntityManager.entityIdSequence,
      immutableEntityManager.nextEntityId,
      immutableEntityManager.ruleEngineParameters
    )
  }
}

trait OverrideCreator {
  def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride]
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

trait AbstractPropertyOverride {
  def entityName: String
  def propertyName: String
  def newValue: Long
}
object AbstractPropertyOverride {
  def seqPropertyOverrideToMap(propertyOverrides: Seq[AbstractPropertyOverride]): Map[String, Long] = {
    propertyOverrides.map{ po => (po.propertyName, po.newValue)}.toMap
  }
}

case class ConstantPropertyOverride(entityName: String, propertyName: String, newValue: Long) extends AbstractPropertyOverride

case class PlusPropertyOverride(entityResolver: EntityResolver,
                                entityName: String,
                                propertyName: String,
                                addValue: Long
                               ) extends AbstractPropertyOverride {
  def newValue: Long = {
    entityResolver.getEntityProperty(entityName, propertyName) + addValue
  }
}




abstract class DefaultRule extends AbstractRule with OverrideCreator with Costed with UuidLabeled