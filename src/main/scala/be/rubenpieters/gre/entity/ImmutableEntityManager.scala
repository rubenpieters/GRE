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
                                   ruleEngineParameters: RuleEngineParameters
                                 )
  extends EntityResolver {
  require(currentEntityId < entityMap.size)

  lazy val nextState = entityMap.get(entityIdSequence(currentEntityId)).get.activeRule.apply(this, ruleEngineParameters)
  lazy val nextEntityId = MathUtils.addOneWithWraparound(currentEntityId, entityIdSequence.size)

  def getEntityByName(entityName: String): ImmutableEntity = {
    entityMap(entityName)
  }

  override def getEntityProperty(entityName: String, propertyName: String): Long = {
    getEntityByName(entityName).getPropertyByName(propertyName)
  }
}

object ImmutableEntityManager {
  def entityManagerInit(entities: Seq[ImmutableEntity], ruleEngineParameters: RuleEngineParameters = RuleEngineParameters.default): ImmutableEntityManager = {
    entities match {
      case Seq() => throw new IllegalArgumentException("EntitySequence must have at least one rule to initialize")
      case _ =>
    }
    val entityIdSequence = entities.map{e => e.uniqueId}
    ImmutableEntityManager(entityIdSequence.zip(entities).toMap, entityIdSequence, 0, ruleEngineParameters)
  }
}

trait EntityResolver {
  def getEntityProperty(entityName: String, propertyName: String): Long
}