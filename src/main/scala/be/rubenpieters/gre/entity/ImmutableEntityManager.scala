package be.rubenpieters.gre.entity

import be.rubenpieters.gre.utils.MathUtils

/**
  * Created by rpieters on 14/08/2016.
  */
case class ImmutableEntityManager(entityMap: Map[String, ImmutableEntity], entityIdSequence: Seq[String], currentEntityId: Int) {
  require(currentEntityId < entityMap.size)

  lazy val nextState = entityMap.get(entityIdSequence(currentEntityId)).get.activeRule.apply(this)
  lazy val nextEntityId = MathUtils.addOneWithWraparound(currentEntityId, entityIdSequence.size)

  def getEntityByName(entityName: String): ImmutableEntity = {
    entityMap(entityName)
  }
}

object ImmutableEntityManager {
  def entityManagerInit(entities: Seq[ImmutableEntity]): ImmutableEntityManager = {
    entities match {
      case Seq() => throw new IllegalArgumentException("EntitySequence must have at least one rule to initialize")
      case _ =>
    }
    val entityIdSequence = entities.map{e => e.uniqueId}
    ImmutableEntityManager(entityIdSequence.zip(entities).toMap, entityIdSequence, 0)
  }

  def getEntityProperty(immutableEntityManager: ImmutableEntityManager, entityName: String, propertyName: String): Long = {
    immutableEntityManager.getEntityByName(entityName).getPropertyByName(propertyName)
  }
}
