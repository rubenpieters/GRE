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
    ImmutableEntityManager(updatedEntityMap)
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
  def nextRuleFunc(): (ImmutableEntityManager) => ImmutableEntityManager = {
    val nextRule = ruleSet.ruleSeq(ruleSet.currentRuleId)
    nextRule(uniqueId)
  }
}

case class RuleSet(ruleSeq: Seq[Rule], currentRuleId: Int)

object RuleSet {
  def init(ruleSeq: Seq[Rule]): RuleSet = {
    ruleSeq match {
      case Seq() => throw new IllegalArgumentException("RuleSequence must have atleast one rule to initialize")
      case _ =>
    }
    RuleSet(ruleSeq, 0)
  }
}

case class ImmutableEntityManager(entityMap: Map[String, ImmutableEntity])

object ImmutableEntityManager {
  def init(entities: Seq[ImmutableEntity]): ImmutableEntityManager = {
    ImmutableEntityManager(entities.map{e => (e.uniqueId, e)}.toMap)
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
  val entity = ImmutableEntity("GroupId", "Entity1", Map(), RuleSet.init(ruleSeq))
  val entityManager = ImmutableEntityManager.init(Seq(entity))

  val nextEntityManager = entity.nextRuleFunc().apply(entityManager)

  println(nextEntityManager.entityMap.get("Entity1").get)
}