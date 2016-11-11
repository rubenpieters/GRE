package be.rubenpieters.model.number

import be.rubenpieters.model.{MatchRunner, RunnerState, SimultaneousRoundRunner}
import be.rubenpieters.model.rps.RpsRoundRunner._
import be.rubenpieters.model.rps.{Loss, Tie, Win, _}
import be.rubenpieters.util.ImmutableRng
import cats.data._

/**
  * Created by ruben on 11/11/2016.
  */
object NumberEngineRunner {
  def main(args: Array[String]) = {
    val player1Strategy = NumberStrategy
    val player2Strategy = NumberStrategy

    //    val runResult = RpsRoundRunner.run(player1Strategy, player2Strategy, ())
    //    val winResult = runResult.run((ImmutableRng(5), (0, 0, 0))).value
    //    println(winResult)
    val result = NumberMatchRunner.runX(player1Strategy, player2Strategy, (List(), 1), (ImmutableRng(5), (0, 0, 0)), NumberRoundRunner, 10000)
    println(result.last)
  }
}


object NumberRoundRunner extends SimultaneousRoundRunner[NumInput, NumOutput, NumScore] {

  override def worldUpdate(runOut: (NumOutput, NumOutput), in: NumInput): State[RState, Unit] = State[RState, Unit] { case (rng, score) =>
    val (num1, num2) = runOut
    val toGet = in._2
    val diff1 = Math.abs(num1 - toGet)
    val diff2 = Math.abs(num2 - toGet)
    val updatedScore = if (diff1 == diff2) {
      score.copy(_3 = score._3 + 1)
    } else if (diff1 < diff2) {
      score.copy(_1 = score._1 + 1)
    } else if (diff2 < diff1) {
      score.copy(_2 = score._2 + 1)
    } else {
      sys.error("")
    }
    ((rng, updatedScore), ())
  }

  override def produceInput(): State[(RunnerState, NumScore), NumInput] = (for {
    chosenNum <- ImmutableRng.nextInt(1000)
  } yield (List[Long](1, 2, 3), chosenNum.toLong)
    ).transformS(_._1, (r, rng) => (rng, r._2))
}

object NumberMatchRunner extends MatchRunner[NumInput, NumOutput, RunnerState, NumScore]