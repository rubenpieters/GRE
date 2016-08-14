package be.rubenpieters.gre.utils

import scala.util.Random

/**
  * Created by rpieters on 14/08/2016.
  */
object RngUtils {
  // http://stackoverflow.com/a/3906132
  def randomIntFromTo(from: Int, to: Int, rng: Random): Int = {
    if (from == to) {
      from
    } else if (from < to) {
        from + new Random().nextInt(Math.abs(to - from))
    } else {
      from - new Random().nextInt(Math.abs(to - from))
    }
  }
}
