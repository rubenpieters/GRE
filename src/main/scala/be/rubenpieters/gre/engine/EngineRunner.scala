package be.rubenpieters.gre.engine

import be.rubenpieters.gre.endcondition.EndCondition
import be.rubenpieters.gre.entity.{Entity, EntityManager}
import be.rubenpieters.gre.log.LogListener
import be.rubenpieters.gre.rules.RuleEngineParameters

import scala.util.Random

/**
  * Created by rpieters on 14/05/2016.
  */
class EngineRunner(
                    entityOrder: Seq[String],
                    entities: Set[Entity],
                    logListeners: Set[LogListener],
                    endConditions: Set[EndCondition],
                    seed: Long = System.currentTimeMillis()
                  ) {
  verifyRuleSetCosts()

  val rng = new Random(seed)

  var entityRuleQueue = entityOrder
  val entityManager = new EntityManager()
  entityManager.registerEntities(entities)

  var endConditionReached = false

  val ruleEngineParameters = RuleEngineParameters(entityManager, rng)

  def verifyRuleSetCosts() = {
    val costs = entities.map{e => (e, e.ruleSetCost)}
        .filter (_._2 > 10)
    if (costs.nonEmpty) {
      throw new IllegalStateException("Entities " + costs.map(_._1.uniqueId) + " have illegal costs of " + costs.map(_._2))
    }
  }

  def runStep() = {
    if (! endConditionReached) {
      val fromEntity = nextEntity()
      executeRule(fromEntity)
      checkEndConditions()
    } else {
      log("End condition reached")
    }
  }

  def runUntilEndConditionReached() = {
    while (! endConditionReached) {
      runStep()
    }
  }

  def executeRule(entity: Entity) = {
    val currentRule = entity.popRule()
    val line = currentRule.applyAndPrependRuleLabel(entity, ruleEngineParameters)
    log(line)
  }

  def checkEndConditions() = {
    val conditionChecks = endConditions
      .flatMap(_.checkCondition(this))
    if (conditionChecks.nonEmpty) {
      endConditionReached = true
      conditionChecks.foreach(log)
    }
  }

  def updateQueue() = {
    entityRuleQueue = entityRuleQueue.drop(1)
    if (entityRuleQueue.isEmpty) {
      entityRuleQueue = entityOrder
    }
  }

  def nextEntity(): Entity = {
    val nextEntityId = entityRuleQueue.head
    updateQueue()
    entityManager.getEntity(nextEntityId)
  }

  def log(line: String) = {
    logListeners.foreach(_.log(line))
  }
}