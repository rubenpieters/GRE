package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.RuleEngineParameters
import be.rubenpieters.gre.utils.MathUtils

/**
  * Created by rpieters on 14/08/2016.
  */
case class ImmutableEntityManager(
                                   entityMap: Map[String, ImmutableEntity],
                                   entityIdSequence: Seq[String],
                                   currentEntityId: Int,
                                   ruleEngineParameters: RuleEngineParameters,
                                   globalEffects: GlobalEffectEntityLike
                                 )
  extends EntityResolver {
  require(currentEntityId < entityMap.size)

  val currentEntity = entityMap(entityIdSequence(currentEntityId))
  lazy val nextActiveRule = currentEntity.activeRule
  lazy val nextRule = currentEntity.activeAppliedRule
  lazy val nextState = applyRuleAndAdvanceState(nextRule)
  lazy val nextEntityId = MathUtils.addOneWithWraparound(currentEntityId, entityIdSequence.size)

  def getEntityByName(entityName: String): ImmutableEntity = {
    entityMap(entityName)
  }

  override def getEntityProperty(entityName: String, propertyName: String): Long = {
    getEntityByName(entityName).getPropertyByName(propertyName)
  }

  def applyRule(rule: (ImmutableEntityManager, RuleEngineParameters) => Map[String, ImmutableEntity]): ImmutableEntityManager = {
    val (updatedEntityMap, updatedGlobalEffects) = rule.apply(this, ruleEngineParameters)

    ImmutableEntityManager(
      updatedEntityMap,
      entityIdSequence,
      currentEntityId,
      ruleEngineParameters,
      updatedGlobalEffects
    )
  }

  def applyRuleAndAdvanceState(rule: (ImmutableEntityManager, RuleEngineParameters) => Map[String, ImmutableEntity]): ImmutableEntityManager = {
    // update rule counter of fromEntity
    val fromEntityName = currentEntity.uniqueId
    val fromEntityWithIncrRuleCounter = Map(fromEntityName -> currentEntity.withIncrRuleCounter)

    val (updatedEntityMap, updatedGlobalEffects) = rule.apply(this, ruleEngineParameters) ++ fromEntityWithIncrRuleCounter

    ImmutableEntityManager(
      updatedEntityMap,
      entityIdSequence,
      nextEntityId,
      ruleEngineParameters,
      updatedGlobalEffects
    )
  }

  // TODO: create apply global effects method, which applies all rules in the global effects and decreases the counters
}

object ImmutableEntityManager {
  def entityManagerInit(entities: Seq[ImmutableEntity], ruleEngineParameters: RuleEngineParameters = RuleEngineParameters.default): ImmutableEntityManager = {
    entities match {
      case Seq() => throw new IllegalArgumentException("EntitySequence must have at least one rule to initialize")
      case _ =>
    }
    val entityIdSequence = entities.map{e => e.uniqueId}
    ImmutableEntityManager(entityIdSequence.zip(entities).toMap, entityIdSequence, 0, ruleEngineParameters, new GlobalEffects())
  }
}

trait EntityResolver {
  def getEntityProperty(entityName: String, propertyName: String): Long
}