package be.rubenpieters.gre.rules

import java.util.UUID

import be.rubenpieters.gre.entity._
import be.rubenpieters.gre.utils.MathUtils
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
  * Created by rpieters on 14/05/2016.
  */
abstract class AbstractRule {
  this: OverrideCreator with EffectCreator with Costed with Labeled =>
  val logger = Logger(LoggerFactory.getLogger("SingleRun"))
  def label: String

  def apply(fromEntityName: String)(immutableEntityManager: ImmutableEntityManager,
                                    ruleEngineParameters: RuleEngineParameters)
  : (Map[String, ImmutableEntity], GlobalEffectEntityLike) = {
    logger.debug(s"$fromEntityName executing $label")

    // apply effects


    // apply overrides
    val propertyOverrides = createOverrides(fromEntityName, immutableEntityManager, ruleEngineParameters)
    //propertyOverrides.foreach { x => logger.debug(s"override $x") }

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

    updatedEntityMap
  }
}

case class IfElseFumbleRule(
                             ifCondition: (String, EntityResolver, RuleEngineParameters) => Boolean,
                             ifRule: OverrideCreator,
                             override val label: String
                           ) extends DefaultRule {


  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    if (ifCondition.apply(fromEntityId, entityResolver, ruleEngineParameters)) {
      ifRule.createOverrides(fromEntityId, entityResolver, ruleEngineParameters)
    } else {
      Seq()
    }
  }
}

trait EffectCreator {
  def createEffects(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[Effect] = {
    Seq()
  }
}

trait OverrideCreator {
  def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    Seq()
  }
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

case class MinusPropertyOverride(entityResolver: EntityResolver,
                                 entityName: String,
                                 propertyName: String,
                                 minusValue: Long
                                ) extends AbstractPropertyOverride {
  lazy val newValue: Long = entityResolver.getEntityProperty(entityName, propertyName) - minusValue

  override def toString: String = {
    s"MinusPropertyOverride(enNm: $entityName, prNm: $propertyName, minusv: $minusValue, nv: $newValue)"
  }
}

case class ClampedPlusPropertyOverride(entityResolver: EntityResolver,
                                       entityName: String,
                                       propertyName: String,
                                       addValue: Long,
                                       maxValue: Long
                                      ) extends AbstractPropertyOverride {
  require(addValue >= 0)
  lazy val oldValue = entityResolver.getEntityProperty(entityName, propertyName)
  lazy val newValue: Long = MathUtils.clampedPlus(oldValue, addValue, maxValue)

  override def toString: String = {
    s"ClampedPlusPropertyOverride(enNm: $entityName, prNm: $propertyName, av: $addValue, mv: $maxValue, nv: $newValue)"
  }
}

case class ClampedMinusPropertyOverride(entityResolver: EntityResolver,
                                        entityName: String,
                                        propertyName: String,
                                        minusValue: Long,
                                        minValue: Long
                                       ) extends AbstractPropertyOverride {
  require(minusValue >= 0)
  lazy val oldValue = entityResolver.getEntityProperty(entityName, propertyName)
  lazy val newValue: Long = MathUtils.clampedMinus(oldValue, minusValue, minValue)

  override def toString: String = {
    s"ClampedMinusPropertyOverride(enNm: $entityName, prNm: $propertyName, minusv: $minusValue, minv: $minValue, nv: $newValue)"
  }
}

abstract class DefaultRule extends AbstractRule with OverrideCreator with EffectCreator with Costed with UuidLabeled