package be.rubenpieters.gre.rules

import java.util.UUID


/**
  * Created by rpieters on 7/08/2016.
  */

// this is code im playing with, to be integrated with or replace the corresponding classes

abstract class Rule {
  this: OverrideCreator with Costed with Labeled =>

  def apply(fromEntityId: String)(immutableEntityManager: ImmutableEntityManager) = {
    val propertyOverrides = createOverrides(fromEntityId, immutableEntityManager)

    val propertyOverridesPerEntity = propertyOverrides.groupBy(_.entityName)
    val updatedEntityMap = propertyOverridesPerEntity.map { case (entityName, propertyOverrideSeq) =>
      val entity = immutableEntityManager.entityMap.get(entityName).get
      (entityName,
        ImmutableEntity(
          entity.groupId,
          entity.uniqueId,
          entity.properties ++ PropertyOverride.seqPropertyOverrideToMap(propertyOverrideSeq),
          entity.ruleSet
        ))
    }
    ImmutableEntityManager(immutableEntityManager.entityMap ++ updatedEntityMap, immutableEntityManager.entityIdSequence, immutableEntityManager.nextEntityId)
  }
}

trait OverrideCreator {
  def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride]
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

case class PropertyOverride(entityName: String, propertyName: String, oldValue: Option[Long], newValue: Long) {

}


object PropertyOverride {
  def seqPropertyOverrideToMap(propertyOverrides: Seq[PropertyOverride]): Map[String, Long] = {
    propertyOverrides.map{ po => (po.propertyName, po.newValue)}.toMap
  }
}

case class ImmutableEntity(
                            groupId: String,
                            uniqueId: String,
                            properties: Map[String, Long],
                            ruleSet: RuleSet
                          ) {
  def nextRuleFunc: (ImmutableEntityManager) => ImmutableEntityManager = {
    val nextRule = ruleSet.ruleSeq(ruleSet.currentRuleId)
    nextRule(uniqueId)
  }
}

case class RuleSet(ruleSeq: Seq[Rule], currentRuleId: Int)

object RuleSet {
  def init(ruleSeq: Seq[Rule]): RuleSet = {
    ruleSeq match {
      case Seq() => throw new IllegalArgumentException("RuleSequence must have at least one rule to initialize")
      case _ =>
    }
    RuleSet(ruleSeq, 0)
  }
}

case class ImmutableEntityManager(entityMap: Map[String, ImmutableEntity], entityIdSequence: Seq[String], currentEntityId: Int) {
  def nextRuleFunc: (ImmutableEntityManager) => ImmutableEntityManager = {
    entityMap.get(entityIdSequence(currentEntityId)).get.nextRuleFunc
  }

  def applyNextRule: ImmutableEntityManager = {
    val nextRuleFunc = entityMap.get(entityIdSequence(currentEntityId)).get.nextRuleFunc
    nextRuleFunc.apply(this)
  }

  def nextEntityId = {
    val tryNextId = currentEntityId + 1
    println("TRY: " + tryNextId)
    if (tryNextId >= entityIdSequence.size) {
      println("F")
      0
    } else {
      println("T")
      tryNextId
    }
  }
}

object ImmutableEntityManager {
  def init(entities: Seq[ImmutableEntity]): ImmutableEntityManager = {
    entities match {
      case Seq() => throw new IllegalArgumentException("EntitySequence must have at least one rule to initialize")
      case _ =>
    }
    val entityIdSequence = entities.map{e => e.uniqueId}
    ImmutableEntityManager(entityIdSequence.zip(entities).toMap, entityIdSequence, 0)
  }
}

object Adhoc extends App {
  object InitializeRule extends Rule with OverrideCreator with Costed with Labeled {
    override def label = "InitRule"

    override def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride] = {
      Seq(
        PropertyOverride(fromEntityId, "A", None, 1)
        ,PropertyOverride(fromEntityId, "B", None, 2)
      )
    }
  }

  val ruleSeq: Seq[Rule] = Seq(InitializeRule)
  val entity1 = ImmutableEntity("GroupId", "Entity1", Map(), RuleSet.init(ruleSeq))
  val entity2 = ImmutableEntity("GroupId", "Entity2", Map(), RuleSet.init(ruleSeq))
  val entityManager = ImmutableEntityManager.init(Seq(entity1, entity2))

  val entityManager1 = entityManager.applyNextRule

  println(entityManager1.entityMap.get("Entity1").get)
  println(entityManager1.entityMap.get("Entity2").get)
  println(entityManager1.nextEntityId)

  val entityManager2 = entityManager1.applyNextRule

  println(entityManager2.entityMap.get("Entity1").get)
  println(entityManager2.entityMap.get("Entity2").get)
  println(entityManager2.nextEntityId)
}