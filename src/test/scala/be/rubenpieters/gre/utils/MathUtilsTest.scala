package be.rubenpieters.gre.utils

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by rpieters on 14/08/2016.
  */
class MathUtilsTest extends FlatSpec with Matchers {
  "addWithWraparound" should "work in basic cases" in {
    MathUtils.addWithWraparound(5, 3, 10, 1) shouldEqual 8
    MathUtils.addWithWraparound(5, 5, 10, 1) shouldEqual 1
    MathUtils.addWithWraparound(5, 6, 10, 1) shouldEqual 1
  }
}
