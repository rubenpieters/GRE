package be.rubenpieters.gre.rules

import java.util.UUID

import be.rubenpieters.gre.entity.{EntityResolver, ImmutableEntity, ImmutableEntityManager}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
  * Created by rpieters on 14/05/2016.
  */
abstract class AbstractRule {
  this: OverrideCreator with Costed with Labeled =>
  val logger = Logger(LoggerFactory.getLogger("SingleRun"))

  def apply(fromEntityName: String)(immutableEntityManager: ImmutableEntityManager, ruleEngineParameters: RuleEngineParameters) = {
    logger.debug(s"$fromEntityName executing $label")

    val propertyOverrides = createOverrides(fromEntityName, immutableEntityManager, ruleEngineParameters)
    propertyOverrides.foreach { x => logger.debug(s"override $x") }

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
  lazy val newValue: Long = entityResolver.getEntityProperty(entityName, propertyName) + addValue

  override def toString: String = {
    s"PlusPropertyOverride(enNm: $entityName, prNm: $propertyName, av: $addValue, nv: $newValue)"
  }
}

case class MaxClampedPlusPropertyOverride(entityResolver: EntityResolver,
                                          entityName: String,
                                          propertyName: String,
                                          addValue: Long,
                                          maxValue: Long
                                       ) extends AbstractPropertyOverride {
  lazy val oldValue = entityResolver.getEntityProperty(entityName, propertyName)
  lazy val newValue: Long = if (oldValue > maxValue - addValue) {
    maxValue
  } else {
    oldValue + addValue
  }

  override def toString: String = {
    s"MaxClampedPlusPropertyOverride(enNm: $entityName, prNm: $propertyName, av: $addValue, mv: $maxValue, nv: $newValue)"
  }
}


abstract class DefaultRule extends AbstractRule with OverrideCreator with Costed with UuidLabeled