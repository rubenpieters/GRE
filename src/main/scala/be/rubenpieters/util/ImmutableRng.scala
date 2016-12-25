package be.rubenpieters.util

import cats.data.State
import cats._
import cats.implicits._
import cats.data._

import scala.annotation.tailrec
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Created by ruben on 31/10/2016.
  */
case class ImmutableRng(seed: Long) {
  def nextLong(): (ImmutableRng, Long) =
    ImmutableRng.nextLong().run(this).value

  def nextInt(bound: Int): (ImmutableRng, Int) =
    ImmutableRng.nextInt(bound).run(this).value

  def chooseOne[A](choices: Map[A, Int]): (ImmutableRng, A) =
    ImmutableRng.chooseOne(choices).run(this).value

  def shuffle[T, CC[X] <: TraversableOnce[X]](xs: CC[T])(implicit bf: CanBuildFrom[CC[T], T, CC[T]]): (ImmutableRng, CC[T]) =
    ImmutableRng.shuffle(xs).run(this).value
}

object ImmutableRng {
  // implementations copied and adapted from java.util.Random
  private val multiplier: Long = 0x5DEECE66DL
  private val addend: Long = 0xBL
  private val mask: Long = (1L << 48) - 1

  def scramble(seed: Long): Long = (seed ^ multiplier) & mask

  def scrambled(initialSeed: Long): ImmutableRng = ImmutableRng(scramble(initialSeed))

  def next(bits: Int) = State[ImmutableRng, Int] { rng: ImmutableRng =>
    val nextSeed = (rng.seed * multiplier + addend) & mask
    val randomBits = (nextSeed >>> (48 - bits)).toInt
    (ImmutableRng(nextSeed), randomBits)
  }

  def nextWhile(bits: Int, randomBits: Int, condition: Int => Boolean): State[ImmutableRng, Int] = {
    if (condition(randomBits)) {
      for {
        nextRandomBits <- next(bits)
        result <- nextWhile(bits, nextRandomBits, condition)
      } yield result
    } else {
      State.pure(randomBits)
    }
  }

  def nextLong(): State[ImmutableRng, Long] = for {
    x <- next(32)
    y <- next(32)
  } yield (x.toLong << 32) + y

  def nextInt(bound: Int): State[ImmutableRng, Int] = {
    require(bound > 0)

    val initialR = next(31)
    val m = bound - 1

    for {
      r <- initialR
      updatedR <- if ((bound & m) == 0) {
        initialR.map(x => ((bound * x.toLong) >> 31).toInt)
      } else {
        nextWhile(31, r, x => x - (x % bound) + m < 0).map(_ % bound)
      }
    } yield updatedR
  }

  def chooseOne[A](choices: Map[A, Int]): State[ImmutableRng, A] = {
    require(choices.values.forall(_ > 0))
    val choicesList = choices.toList
    val sums = choicesList.map(_._2).scan(0)(_ + _)
    val sum = sums.last
    // check against overflow
    require(sum > 0)

    for {
      randomInt <- nextInt(sum)
      randomIndex = sums.indexWhere(x => randomInt < x) - 1
    } yield choicesList(randomIndex)._1
  }

  def shuffle[T, CC[X] <: TraversableOnce[X]](xs: CC[T])(implicit bf: CanBuildFrom[CC[T], T, CC[T]]): State[ImmutableRng, CC[T]] = {
    def swap(xs: CC[T], i1: Int, i2: Int) = {
      val buf = new ArrayBuffer[T] ++= xs

      def swap(i1: Int, i2: Int) {
        val tmp = buf(i1)
        buf(i1) = buf(i2)
        buf(i2) = tmp
      }

      swap(i1, i2)

      (bf(xs) ++= buf).result()
    }

    def _shuffle(xs: CC[T], n: Int): State[ImmutableRng, CC[T]] =
      if (n == 1) {
        State.pure(xs)
      } else {
        for {
          k <- nextInt(n)
          currState <- State.get[ImmutableRng]
//          _ = println(s"K: $k, bound $n, $currState")
          result <- {
            val shuffledXs = swap(xs, n - 1, k)
            _shuffle(shuffledXs, n - 1)
          }
        } yield result
      }

    if (xs.isEmpty) {
      State.pure(xs)
    } else {
      _shuffle(xs, xs.size)
    }
  }
}