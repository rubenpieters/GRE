package be.rubenpieters.gre.engine

import be.rubenpieters.gre.entity.{Entity, EntityManager}
import be.rubenpieters.gre.log.LogListener

/**
  * Created by rpieters on 14/05/2016.
  */
class EngineRunner(
                    entityOrder: Seq[String],
                    entities: Set[Entity],
                    logListeners: Set[LogListener]
                  ) {
  var entityRuleQueue = entityOrder
  val entityManager = new EntityManager()
  entityManager.registerEntities(entities)

  def runStep() = {
    val fromEntity = nextEntity()
    val currentRule = fromEntity.popRule()
    val logLine = currentRule.apply(fromEntity, entityManager)
    logListeners.foreach(_.log(logLine))
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
}
