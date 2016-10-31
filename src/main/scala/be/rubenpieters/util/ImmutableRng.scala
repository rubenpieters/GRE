package be.rubenpieters.util

import java.util.concurrent.atomic.AtomicLong

import cats.data.State

/**
  * Created by ruben on 31/10/2016.
  */
case class ImmutableRng(seed: Long) {
  def nextLong(): (ImmutableRng, Long) =
    ImmutableRng.nextLong().run(this).value
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

  def nextLong(): State[ImmutableRng, Long] = for {
    x <- next(32)
    y <- next(32)
  } yield (x.toLong << 32) + y
}