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

  "effect based on running counter" should "work correctly" in {
    val entity = baseEntity.withNew(newProperties = Map(), newAppliedEffects = Map(
      "x" -> ("BASE", IdleEffect(TestEffectBasedOnRunningCounter(EffectRunning(2), "x")))
    ))
    val entityStates = (1 to 3).map { x =>
      (1 to x).foldLeft(entity)((entityAcc, _) => entityAcc.applyEffects)
    }

    entityStates.foreach(println)
  }

  "effect creating effects" should "work correctly" in {
    val entity = baseEntity.withNew(newProperties = Map("x" -> 0), newAppliedEffects = Map(
      "x" -> ("BASE", IdleEffect(TestEffectCreatingEffects(EffectRunning(2))))
    ))
    val entityStates = (1 to 9).map { x =>
      (1 to x).foldLeft(entity)((entityAcc, _) => entityAcc.applyEffects)
    }

    entityStates.foreach(println)
  }

  case class TestEffectBasedOnRunningCounter(effectState: EffectState, property: String) extends Effect(effectState) {
    override def createWithNewState(effectState: EffectState): Effect = TestEffectBasedOnRunningCounter(effectState, property)

    override def createOperations(actingEntity: EntityId, targetEntity: Entity,
                                  entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[Operation] = {

      effectState match {
        case EffectRunning(i) => Seq(ConstantPropertyOverride("BASE", property, i))
        case EffectEnding => Seq(ConstantPropertyOverride("BASE", property, -100))
      }
    }
  }

  case class TestEffectCreatingEffects(effectState: EffectState) extends Effect(effectState) {
    override def createWithNewState(effectState: EffectState): Effect = TestEffectCreatingEffects(effectState)

    override def createOperations(actingEntity: EntityId, targetEntity: Entity,
                                  entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[Operation] = {

      effectState match {
        case EffectRunning(i) => Seq(SimpleAddEffectOperation(TestEffectBasedOnRunningCounter(EffectRunning(2), s"x_$i"), "BASE"))
        case EffectEnding => Seq()
      }
    }
  }
}
