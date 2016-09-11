package be.rubenpieters.gre.utils

import scala.util.Random

/**
  * Created by ruben on 11/09/16.
  */
object RngUtils {
  // http://stackoverflow.com/a/3906132
  def randomIntFromTo(from: Int, to: Int, rng: Random): Int = {
    if (from == to) {
      from
    } else if (from < to) {
      from + rng.nextInt(Math.abs(to - from))
    } else {
      from - rng.nextInt(Math.abs(to - from))
    }
  }
}
