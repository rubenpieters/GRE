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

  def applyEffects(): ImmutableEntityManager = {
    // TODO: create effect class and have it provide a method which gives
    // 1- updated entities
    // 2- created effects
    // 3- its next effect state
    val nextEffects = globalEffects.effects.flatMap { case (fromEntityId, effectRule, endRule, effectState) =>
      effectState match {
        case EffectFinished =>
          val updatedEntities = endRule.apply(fromEntityId)(this, ruleEngineParameters)
          None
        case EffectRunning(counter) =>
          val updatedEntities = effectRule.apply(fromEntityId)(this, ruleEngineParameters)
          Some((fromEntityId, effectRule, endRule, EffectRunning(counter).next))
      }
    }

    val nextGlobalEffects = new GlobalEffects(nextEffects)

    ImmutableEntityManager(
      entityMap,
      entityIdSequence,
      currentEntityId,
      ruleEngineParameters,
      nextGlobalEffects
    )
  }
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