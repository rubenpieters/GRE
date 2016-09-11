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

    entity.entitiesByProperty("X").toList should contain inOrder (entity2, entity1)
  }

  "advance" should "work in basic case" in {
    val entity1 = Entity(
      "p1"
      ,Map("x" -> 0, "y" -> 0, "INITIATIVE"-> 1, "IN_INC" -> 1, "IN_DEC" -> 1)
      ,Map()
      ,Map()
      ,RuleEngineParameters.newParameters
      ,CyclicRuleStrategy(
        Seq(
          new AbstractRule {
            override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
              Seq(
                ("p1", PlusPropertyOverride(entityResolver, "p1", "x", 1))
              )
            }
          },
          new AbstractRule {
            override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
              Seq(
                ("p1", PlusPropertyOverride(entityResolver, "p1", "y", 1))
              )
            }
          }
        )
      )
    )


    val entity2 = Entity(
      "p2"
      ,Map("z" -> 0, "INITIATIVE"-> 2, "IN_INC" -> 2, "IN_DEC" -> 2)
      ,Map()
      ,Map()
      ,RuleEngineParameters.newParameters
      ,CyclicRuleStrategy(
        Seq(
          new AbstractRule {
            override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
              Seq(
                ("p2", PlusPropertyOverride(entityResolver, "p2", "z", 1))
              )
            }
          }
        )
      )
    )

    val scopeEntity = Entity(
      "scope"
      ,Map()
      ,Map("p1" -> entity1, "p2" -> entity2)
      ,Map()
      ,RuleEngineParameters.newParameters
      ,null
    )

    val entityStream = Stream.iterate(scopeEntity){
      entity =>
        entity.advance.asInstanceOf[Entity]
    }

    entityStream(1).getEntityProperty("p2", "z") shouldBe 1
    entityStream(2).getEntityProperty("p2", "z") shouldBe 1
    entityStream(3).getEntityProperty("p2", "z") shouldBe 2
    entityStream(4).getEntityProperty("p2", "z") shouldBe 2
    entityStream(1).getEntityProperty("p1", "x") shouldBe 0
    entityStream(2).getEntityProperty("p1", "x") shouldBe 1
    entityStream(3).getEntityProperty("p1", "x") shouldBe 1
    entityStream(4).getEntityProperty("p1", "x") shouldBe 1
    entityStream(1).getEntityProperty("p1", "y") shouldBe 0
    entityStream(2).getEntityProperty("p1", "y") shouldBe 0
    entityStream(3).getEntityProperty("p1", "y") shouldBe 0
    entityStream(4).getEntityProperty("p1", "y") shouldBe 1
  }
}
