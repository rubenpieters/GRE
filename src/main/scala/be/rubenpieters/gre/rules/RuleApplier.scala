package be.rubenpieters.gre.rules

import be.rubenpieters.gre.entity.{ImmutableEntity, ImmutableEntityManager}


/**
  * Created by rpieters on 7/08/2016.
  */


object Adhoc extends App {
  object InitializeRule extends DefaultRule with Labeled {
    override def label = "InitRule"

    override def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride] = {
      Seq(
        PropertyOverride(fromEntityId, "A", 1)
        ,PropertyOverride(fromEntityId, "B", 2)
      )
    }
  }

  val ruleSeq: Seq[AbstractRule] = Seq(InitializeRule)
  val entity1 = ImmutableEntity("GroupId", "Entity1", Map(), RuleSet.init(ruleSeq))
  val entity2 = ImmutableEntity("GroupId", "Entity2", Map(), RuleSet.init(ruleSeq))
  val entityManager = ImmutableEntityManager.entityManagerInit(Seq(entity1, entity2))

  val entityManager1 = entityManager.nextState

  println(entityManager1.entityMap.get("Entity1").get)
  println(entityManager1.entityMap.get("Entity2").get)
  println(entityManager1.nextEntityId)

  val entityManager2 = entityManager1.nextState

  println(entityManager2.entityMap.get("Entity1").get)
  println(entityManager2.entityMap.get("Entity2").get)
  println(entityManager2.nextEntityId)
}