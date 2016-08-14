package be.rubenpieters.gre.rules

import be.rubenpieters.gre.entity.EntityResolver
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by rpieters on 14/08/2016.
  */
class PlusPropertyOverrideTest extends FlatSpec with Matchers {
  "PlusPropertyOverride" should "work in basic addition case" in {
    PlusPropertyOverride(TestEntityResolverAlways5, "", "", 2).newValue shouldEqual 7
  }

  "PlusPropertyOverride" should "work in basic subtraction case" in {
    PlusPropertyOverride(TestEntityResolverAlways5, "", "", - 2).newValue shouldEqual 3
  }

  object TestEntityResolverAlways5 extends EntityResolver {
    override def getEntityProperty(entityName: String, propertyName: String): Long = 5
  }
}
