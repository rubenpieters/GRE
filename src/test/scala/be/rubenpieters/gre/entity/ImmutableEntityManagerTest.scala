package be.rubenpieters.gre.entity

import be.rubenpieters.gre.entity.ImmutableEntityManager._
import be.rubenpieters.gre.rules._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by rpieters on 14/08/2016.
  */
class ImmutableEntityManagerTest extends FlatSpec with Matchers with MockitoSugar {
  "entityManagerInit" should "give exception when given empty entity sequence" in {
    an [IllegalArgumentException] should be thrownBy {
      entityManagerInit(Seq())
    }
  }

  "nextState" should "work in with test add one rule" in {
    val state1 = entityManagerInit(Seq(ImmutableEntity("GroupId", "Entity1", Map("A"-> 0), RuleSet.init(Seq(TestAddOneRule)))))
    val state2 = state1.nextState
    val state3 = state2.nextState

    state1.getEntityProperty("Entity1", "A") shouldEqual 0L
    state2.getEntityProperty("Entity1", "A") shouldEqual 1L
    state3.getEntityProperty("Entity1", "A") shouldEqual 2L
  }

  "rules" should "correctly advance in next states" in {
    val state1 = entityManagerInit(Seq(ImmutableEntity("GroupId", "Entity1", Map("A"-> 0), RuleSet.init(Seq(TestAddOneRule, TestRemoveOneRule)))))
    val state2 = state1.nextState
    val state3 = state2.nextState

    state1.getEntityProperty("Entity1", "A") shouldEqual 0L
    state2.getEntityProperty("Entity1", "A") shouldEqual 1L
    state3.getEntityProperty("Entity1", "A") shouldEqual 0L
  }

  object TestAddOneRule extends DefaultRule {
    override def label = "InitRule"

    override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[ConstantPropertyOverride] = {
      Seq(
        ConstantPropertyOverride(fromEntityId, "A", entityResolver.getEntityProperty(fromEntityId, "A") + 1)
      )
    }
  }

  object TestRemoveOneRule extends DefaultRule {
    override def label = "InitRule"

    override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[ConstantPropertyOverride] = {
      Seq(
        ConstantPropertyOverride(fromEntityId, "A", entityResolver.getEntityProperty(fromEntityId, "A") - 1)
      )
    }
  }
}


