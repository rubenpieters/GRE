package be.rubenpieters.gre.entity

import be.rubenpieters.gre.entity.ImmutableEntityManager._
import be.rubenpieters.gre.rules._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by rpieters on 14/08/2016.
  */
class ImmutableEntityManagerTest extends FlatSpec with Matchers {
  "entityManagerInit" should "give exception when given empty entity sequence" in {
    an [IllegalArgumentException] should be thrownBy {
      entityManagerInit(Seq())
    }
  }

  "nextState" should "work in with test add one rule" in {
    val state1 = entityManagerInit(Seq(ImmutableEntity("GroupId", "Entity1", Map("A"-> 0), RuleSet.init(Seq(TestAddOneRule)))))
    val state2 = state1.nextState
    val state3 = state2.nextState

    getEntityProperty(state2, "Entity1", "A") shouldEqual 1L
    getEntityProperty(state3, "Entity1", "A") shouldEqual 2L
  }
}


object TestAddOneRule extends DefaultRule {
  override def label = "InitRule"

  override def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride] = {
    Seq(
      PropertyOverride(fromEntityId, "A", getEntityProperty(immutableEntityManager, fromEntityId, "A") + 1)
    )
  }
}
