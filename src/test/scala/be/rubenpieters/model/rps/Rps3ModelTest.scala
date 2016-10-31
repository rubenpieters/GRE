package be.rubenpieters.model.rps

import org.scalatest.{FlatSpec, Matchers}
import be.rubenpieters.model.rps.RpsRules.ops._

/**
  * Created by ruben on 31/10/2016.
  */
class Rps3ModelTest extends FlatSpec with Matchers {
  "rules for rock" should "be correct" in {
    Rock.beats(Rock) shouldEqual Tie
    Rock.beats(Paper) shouldEqual Loss
    Rock.beats(Scissors) shouldEqual Win
  }

  "rules for paper" should "be correct" in {
    Paper.beats(Rock) shouldEqual Win
    Paper.beats(Paper) shouldEqual Tie
    Paper.beats(Scissors) shouldEqual Loss
  }

  "rules for scissors" should "be correct" in {
    Scissors.beats(Rock) shouldEqual Loss
    Scissors.beats(Paper) shouldEqual Win
    Scissors.beats(Scissors) shouldEqual Tie
  }
}
