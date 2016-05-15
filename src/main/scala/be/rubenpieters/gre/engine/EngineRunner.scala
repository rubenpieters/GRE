package be.rubenpieters.gre.engine

import be.rubenpieters.gre.endcondition.EndCondition
import be.rubenpieters.gre.entity.{Entity, EntityManager}
import be.rubenpieters.gre.log.LogListener

/**
  * Created by rpieters on 14/05/2016.
  */
class EngineRunner(
                    entityOrder: Seq[String],
                    entities: Set[Entity],
                    logListeners: Set[LogListener],
                    endConditions: Set[EndCondition]
                  ) {
  var entityRuleQueue = entityOrder
  val entityManager = new EntityManager()
  entityManager.registerEntities(entities)

  var endConditionReached = false

  def runStep() = {
    if (! endConditionReached) {
      val fromEntity = nextEntity()
      executeRule(fromEntity)
      checkEndConditions()
    } else {
      log("End condition reached")
    }
  }

  def executeRule(entity: Entity) = {
    val currentRule = entity.popRule()
    val line = currentRule.apply(entity, entityManager)
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
