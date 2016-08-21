package be.rubenpieters.gre.rules

import be.rubenpieters.gre.entity.EntityResolver
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 21/08/2016.
  */
class ClampedMinusPropertyOverrideTest extends FlatSpec with Matchers {
  "ClampedMinusPropertyOverride" should "work when restricted" in {
    ClampedMinusPropertyOverride(TestEntityResolverAlways5, "", "", 2, 1).newValue shouldEqual 3
  }

  "ClampedMinusPropertyOverride" should "work when not restricted" in {
    ClampedMinusPropertyOverride(TestEntityResolverAlways5, "", "", 8, 2).newValue shouldEqual 2
  }

  object TestEntityResolverAlways5 extends EntityResolver {
    override def getEntityProperty(entityName: String, propertyName: String): Long = 5
  }
}