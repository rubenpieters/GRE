package be.rubenpieters.util

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 1/11/2016.
  */
class ImmutableRngTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {
  val checkAmount = 10000

  "nextLong" should "output the same values as java.util.Random" in {
    forAll { seed: Long =>
      checkEqualWithJavaRng(seed, javaRng => javaRng.nextLong(), immRng => immRng.nextLong())
    }
  }

  "nextInt" should "output the same values as java.util.Random" in {
    forAll { (seed: Long, bound: Int) =>
      whenever (bound > 0) {
        checkEqualWithJavaRng(seed, javaRng => javaRng.nextInt(bound), immRng => immRng.nextInt(bound))
      }
    }
  }

  def checkEqualWithJavaRng[A](seed: Long, javaOp: java.util.Random => A, immRngOp: ImmutableRng => (ImmutableRng, A)) = {
    val javaUtilRng = new java.util.Random(seed)
    val immutableRng = ImmutableRng.scrambled(seed)
    val valuesJava = List.fill(checkAmount)(javaOp(javaUtilRng))
    val statesImmRng = List.iterate(immRngOp(immutableRng), checkAmount){case (rng, x) => immRngOp(rng)}
    val valuesImmRng = statesImmRng.map(_._2)
    valuesJava shouldEqual valuesImmRng
  }
}
