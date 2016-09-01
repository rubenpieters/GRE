package be.rubenpieters.gre

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 29/08/2016.
  */
class EntityTest extends FlatSpec with Matchers with MockitoSugar {
  val baseEntityId = "BASE"
  val baseEntity = Entity(baseEntityId
    ,ruleEngineParameters = RuleEngineParameters.newParameters
    ,ruleAdvanceStrategy = mock[RuleAdvanceStrategy]
  )

  "properties" should "be returned correctly" in {
    val entity = baseEntity.withNew(newProperties = Map("a" -> 1, "b" -> 2))
    entity.getProperty("a") shouldEqual 1
    entity.getProperty("b") shouldEqual 2
  }

  "entities" should "be returned correctly" in {
    val subEntityY = Entity("y", ruleEngineParameters = RuleEngineParameters.newParameters, ruleAdvanceStrategy = mock[RuleAdvanceStrategy])
    val entity = baseEntity.withNew(newSubEntities = Map("y" -> subEntityY))
    entity.getEntity(baseEntityId) shouldEqual entity
    entity.getEntity("y") shouldEqual subEntityY
  }

  "this with running" should "set all effects to running" in {
    val mockEffects = (1 to 10).map{ i => (i.toString, (baseEntityId, IdleEffect(mock[Effect])))}.toMap
    val withRunningEffects = baseEntity.withNew(newAppliedEffects = mockEffects).withRunningEffects
    withRunningEffects.appliedEffects.map(_._2._2).foreach { effect =>
      effect shouldBe a[RunningEffect]
    }
  }

  "two effects" should "run simultaneously" in {
    val entity = baseEntity.withNew(newProperties = Map(), newAppliedEffects = Map(
      "x" -> (baseEntityId, IdleEffect(TestEffectBasedOnRunningCounter(EffectRunning(2), "x")))
      ,"y" -> (baseEntityId, IdleEffect(TestEffectBasedOnRunningCounter(EffectRunning(2), "y")))
    ))
    val entityStates = (1 to 3).map { x =>
      (1 to x).foldLeft(entity)((entityAcc, _) => entityAcc.applyEffects)
    }

    entityStates(0).getProperty("x") shouldEqual 2
    entityStates(0).getProperty("y") shouldEqual 2
    entityStates(0).appliedEffects.size shouldEqual 2
    entityStates(1).getProperty("x") shouldEqual 1
    entityStates(1).getProperty("y") shouldEqual 1
    entityStates(1).appliedEffects.size shouldEqual 2
    entityStates(2).getProperty("x") shouldEqual -100
    entityStates(2).getProperty("y") shouldEqual -100
    entityStates(2).appliedEffects.size shouldEqual 0
  }

  "effect based on running counter" should "work correctly" in {
    val entity = baseEntity.withNew(newProperties = Map(), newAppliedEffects = Map(
      "x" -> (baseEntityId, IdleEffect(TestEffectBasedOnRunningCounter(EffectRunning(2), "x")))
    ))
    val entityStates = (1 to 3).map { x =>
      (1 to x).foldLeft(entity)((entityAcc, _) => entityAcc.applyEffects)
    }

    entityStates(0).getProperty("x") shouldEqual 2
    entityStates(0).appliedEffects.size shouldEqual 1
    entityStates(1).getProperty("x") shouldEqual 1
    entityStates(1).appliedEffects.size shouldEqual 1
    entityStates(2).getProperty("x") shouldEqual -100
    entityStates(2).appliedEffects.size shouldEqual 0
  }

  "effect creating effects" should "work correctly" in {
    val entity = baseEntity.withNew(newProperties = Map(), newAppliedEffects = Map(
      "x" -> (baseEntityId, IdleEffect(TestEffectCreatingEffects(EffectRunning(2))))
    ))
    val entityStates = (1 to 5).map { x =>
      (1 to x).foldLeft(entity)((entityAcc, _) => entityAcc.applyEffects)
    }

    entityStates(0).appliedEffects.size shouldEqual 2
    entityStates(1).getProperty("x_2") shouldEqual 2
    entityStates(1).appliedEffects.size shouldEqual 3
    entityStates(2).getProperty("x_2") shouldEqual 1
    entityStates(2).getProperty("x_1") shouldEqual 2
    entityStates(2).appliedEffects.size shouldEqual 2
    entityStates(3).getProperty("x_2") shouldEqual -100
    entityStates(3).getProperty("x_1") shouldEqual 1
    entityStates(3).appliedEffects.size shouldEqual 1
    entityStates(4).getProperty("x_2") shouldEqual -100
    entityStates(4).getProperty("x_1") shouldEqual -100
    entityStates(4).appliedEffects.size shouldEqual 0
  }

  "effect cleansing" should "work correctly" in {
    val entity = baseEntity.withNew(newProperties = Map(), newAppliedEffects = Map(
      "x" -> ("BASE", IdleEffect(TestEffectCreatingEffects(EffectRunning(2))))
      ,"y" -> ("BASE", IdleEffect(TestEffectCleanseWhenEnd(EffectRunning(3))))
    ))
    val entityStates = (1 to 4).map { x =>
      (1 to x).foldLeft(entity)((entityAcc, _) => entityAcc.applyEffects)
    }

    entityStates(0).appliedEffects.size shouldEqual 3
    entityStates(1).getProperty("x_2") shouldEqual 2
    entityStates(1).appliedEffects.size shouldEqual 4
    entityStates(2).getProperty("x_2") shouldEqual 1
    entityStates(2).getProperty("x_1") shouldEqual 2
    entityStates(2).appliedEffects.size shouldEqual 3
    entityStates(3).getProperty("x_2") shouldEqual 1
    entityStates(3).getProperty("x_1") shouldEqual 2
    entityStates(3).appliedEffects.size shouldEqual 0
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

  case class TestEffectCleanseWhenEnd(effectState: EffectState) extends Effect(effectState) {
    override def createWithNewState(effectState: EffectState): Effect = TestEffectCleanseWhenEnd(effectState)


    override def createOperations(actingEntity: EntityId, targetEntity: Entity,
                                  entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[Operation] = {

      effectState match {
        case EffectRunning(i) => Seq()
        case EffectEnding => Seq(new Operation {
          override def applyOperation(entity: Entity): Entity = {
            entity.withNew(newAppliedEffects = Map())
          }
        })
      }
    }
  }
}
