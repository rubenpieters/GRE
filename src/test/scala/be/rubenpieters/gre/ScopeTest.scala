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
}
