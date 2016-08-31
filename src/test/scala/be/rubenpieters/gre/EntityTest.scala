package be.rubenpieters.gre

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 29/08/2016.
  */
class EntityTest extends FlatSpec with Matchers {
  val baseEntityId = "BASE"
  val baseEntity = Entity(baseEntityId, ruleEngineParameters = RuleEngineParameters.newParameters)

  "properties" should "be returned correctly" in {
    val entity = baseEntity.withNew(newProperties = Map("a" -> 1, "b" -> 2))
    entity.getProperty("a") shouldEqual 1
    entity.getProperty("b") shouldEqual 2
  }

  "entities" should "be returned correctly" in {
    val subEntityY = Entity("y", ruleEngineParameters = RuleEngineParameters.newParameters)
    val entity = baseEntity.withNew(newSubEntities = Map("y" -> subEntityY))
    entity.getEntity(baseEntityId) shouldEqual entity
    entity.getEntity("y") shouldEqual subEntityY
  }

  "effect implementation" should "work correctly" in {
    val entity = baseEntity.withNew(newProperties = Map("x" -> 0), newAppliedEffects = Map(

    ))
  }

  case class TestEffectBasedOnRunningCounter(effectState: EffectState) extends Effect(effectState) {
    override def createWithNewState(effectState: EffectState): Effect = TestEffectBasedOnRunningCounter(effectState)

    override def createOperations(actingEntity: EntityId, targetEntity: Entity, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[Operation] = ???

  }
}
