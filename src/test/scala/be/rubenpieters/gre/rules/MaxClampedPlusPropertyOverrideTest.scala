package be.rubenpieters.gre.rules

import be.rubenpieters.gre.entity.EntityResolver
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 16/08/2016.
  */
class MaxClampedPlusPropertyOverrideTest extends FlatSpec with Matchers {
  "MaxClampedPlusPropertyOverride" should "work when restricted" in {
    MaxClampedPlusPropertyOverride(TestEntityResolverAlways5, "", "", 2, 6).newValue shouldEqual 6
  }

  "MaxClampedPlusPropertyOverride" should "work when not restricted" in {
    MaxClampedPlusPropertyOverride(TestEntityResolverAlways5, "", "", 2, 8).newValue shouldEqual 7
  }

  object TestEntityResolverAlways5 extends EntityResolver {
    override def getEntityProperty(entityName: String, propertyName: String): Long = 5
  }
}
