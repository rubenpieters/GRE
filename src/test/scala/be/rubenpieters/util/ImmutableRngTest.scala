package be.rubenpieters.util

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

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

//  "nextInt" should "output the same values as java.util.Random with changing bounds" in {
//    forAll { (seed: Long, bound: Int) =>
//      var bound1 = bound
//      var bound2 = bound
//      whenever (bound > 0 && bound < 100000) {
//        checkEqualWithJavaRng(seed, javaRng => {bound1 = bound1 + 1 ; javaRng.nextInt(bound1)}, immRng => {bound2 = bound2 + 1 ; immRng.nextInt(bound2)})
//      }
//    }
//  }
//
//  "shuffle" should "output the same values as scala.util.Random x" in {
//    class PrintRng(x: Long) extends java.util.Random(x) {
//      override def nextInt(n: Int): Int = {
//        val myInt = super.nextInt(n)
//        println(s"INT: $myInt, bound $n")
//        myInt
//      }
//    }
//
//    import Random._
//    val list = List(5, 3, 4, 1, 2)
//    val printRng = new PrintRng(1)
//    println(printRng.shuffle(list))
//    println(ImmutableRng.scrambled(1).shuffle(list))
//
//    val x = for {
//      x5 <- ImmutableRng.nextInt(5)
//      x4 <- ImmutableRng.nextInt(4)
//      x3 <- ImmutableRng.nextInt(3)
//      x2 <- ImmutableRng.nextInt(2)
//    } yield (x5, x4, x3, x2)
//    println(x.run(ImmutableRng.scrambled(1)).value)
//    val rng = new java.util.Random(1)
//    println(rng.nextInt(5))
//    println(rng.nextInt(4))
//    println(rng.nextInt(3))
//    println(rng.nextInt(2))
//  }
//
//  "shuffle" should "output the same values as scala.util.Random" in {
//    import Random._
//    forAll { (seed: Long, list: List[Int]) =>
//      checkEqualWithJavaRng(seed, javaRng => javaRng.shuffle(list), immRng => immRng.shuffle(list))
//    }
//  }

  def checkEqualWithJavaRng[A](seed: Long, javaOp: java.util.Random => A, immRngOp: ImmutableRng => (ImmutableRng, A)) = {
    val javaUtilRng = new java.util.Random(seed)
    val immutableRng = ImmutableRng.scrambled(seed)
    val valuesJava = List.fill(checkAmount)(javaOp(javaUtilRng))
    val statesImmRng = List.iterate(immRngOp(immutableRng), checkAmount){case (rng, x) => immRngOp(rng)}
    val valuesImmRng = statesImmRng.map(_._2)
    valuesJava shouldEqual valuesImmRng
  }
}
