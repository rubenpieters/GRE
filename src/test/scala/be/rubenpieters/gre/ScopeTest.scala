package be.rubenpieters.gre

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 9/09/2016.
  */
class ScopeTest extends FlatSpec with Matchers with MockitoSugar {
  val baseEntityId = "BASE"
  val baseEntity = Entity(baseEntityId
    ,ruleEngineParameters = RuleEngineParameters.newParameters
    ,ruleAdvanceStrategy = mock[RuleAdvanceStrategy]
  )

  "entitiesByProperty" should "give entities in the correct order" in {
    val entity1 = baseEntity.copy(id = "1", Map("X" -> 1L))
    val entity2 = baseEntity.copy(id = "2", Map("X" -> 2L))
    val entity = baseEntity.copy(subEntities = Map(
      "1" -> entity1
      ,"2" -> entity2
    ))

    entity.entitiesByProperty("X").toList should contain inOrder (entity1, entity2)
  }

  "advance" should "work in basic case" in {
    val entity1 = baseEntity.copy(id = "1", Map("X" -> 0L, "INITIATIVE" -> 1L), ruleAdvanceStrategy = CyclicRuleStrategy(
      Seq(
        new AbstractRule {
          override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
            Seq(
              ("1", PlusPropertyOverride(entityResolver, "1", "X", 1))
            )
          }
        })))
    val entity2 = baseEntity.copy(id = "2", Map("X" -> 0L, "INITIATIVE" -> 2L), ruleAdvanceStrategy = CyclicRuleStrategy(
      Seq(
        new AbstractRule {
          override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
            Seq(
              ("2", PlusPropertyOverride(entityResolver, "2", "X", 1))
            )
          }
        })))
    val entity = baseEntity.copy(subEntities = Map(
      "1" -> entity1
      ,"2" -> entity2
    ))

    val advanced1 = entity.advance
    println(advanced1)
    val advanced2 = entity.advance
    println(advanced2)
  }
}
