package be.rubenpieters.model.rps

import be.rubenpieters.model._
import RpsRules.ops._
import be.rubenpieters.util.ImmutableRng
import cats.data._

/**
  * Created by ruben on 31/10/16.
  */
object RpsEngineRunner {
  def main(args: Array[String]) = {
    val player1Strategy = RpsUniformStrategy
    val player2Strategy = RpsUniformStrategy

//    val runResult = RpsRoundRunner.run(player1Strategy, player2Strategy, ())
//    val winResult = runResult.run((ImmutableRng(5), (0, 0, 0))).value
//    println(winResult)
    val result = RpsMatchRunner.runX(player1Strategy, player2Strategy, (), (ImmutableRng(5), (0, 0, 0)), RpsRoundRunner, 10000)
    println(result.last)
  }
}

object RpsRoundRunner extends SimultaneousRoundRunner[RpsInput, RpsOutput, RpsScore] {
  override def worldUpdate(runOut: (RpsOutput, RpsOutput), in: RpsInput): State[RState, Unit] = State[RState, Unit] { case (rng, score) =>
    val (rps1, rps2) = runOut
    val updatedScore = rps1.beats(rps2) match {
      case Win => score.copy(_1 = score._1 + 1)
      case Loss => score.copy(_2 = score._2 + 1)
      case Tie => score.copy(_3 = score._3 + 1)
    }
    ((rng, updatedScore), ())
  }

  override def produceInput(): State[(RunnerState, (Int, Int, Int)), RpsInput] = State.pure(())
}

object RpsMatchRunner extends MatchRunner[RpsInput, RpsOutput, RunnerState, RpsScore]

object RpsWinDeclarer extends WinDeclarer[RpsOutput] {
  def winner(outs: (RpsOutput, RpsOutput), perspective: Int): RpsWinState = {
    val (rps1, rps2) = outs
    perspective match {
      case 1 => rps1.beats(rps2)
      case 2 => rps2.beats(rps1)
    }
  }
}