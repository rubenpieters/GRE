package be.rubenpieters.gre

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 29/08/2016.
  */
class EntityTest extends FlatSpec with Matchers {
  "properties" should "be returned correctly" in {
    val entity = Entity("", Map("a" -> 1, "b" -> 2))
    entity.getProperty("a") shouldEqual 1
    entity.getProperty("b") shouldEqual 2
  }

  "entities" should "be returned correctly" in {
    val subEntityY = Entity("y")
    val entity = Entity("x", Map("a" -> 1, "b" -> 2), Map("y" -> subEntityY))
    entity.getEntity("x") shouldEqual entity
    entity.getEntity("y") shouldEqual subEntityY
  }
}
