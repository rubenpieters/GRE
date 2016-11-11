package be.rubenpieters.model

import be.rubenpieters.model.number.NumInput
import be.rubenpieters.model.rps.RpsWinState
import cats.data._

/**
  * Created by ruben on 31/10/2016.
  */
trait RoundRunner[In, Out, VisibleState, InvisibleState] {
  type RunOut = (Out, Out)
  type RState = (VisibleState, InvisibleState)

  def run(strategy1: Strategy[In, Out, VisibleState], strategy2: Strategy[In, Out, VisibleState], in: In): State[RState, Unit]
  def produceInput(): State[RState, In]
  def worldUpdate(runOut: RunOut, in: In): State[RState, Unit]
}

// TODO: the simultaneous is basically a sort of applicative strategy and the interleaving is a sort of monadic strategy
// maybe should try to find some way to make use of this and make this look nicer
trait SimultaneousRoundRunner[In, Out, InvisibleState] extends RoundRunner[In, Out, RunnerState, InvisibleState] {
  def run(strategy1: Strategy[In, Out, RunnerState], strategy2: Strategy[In, Out, RunnerState], in: In): State[RState, Unit] =
    for {
      input <- produceInput()
      out1 <- strategy1.getAction(input).transformS[RState](_._1, (t, i) => (i, t._2))
      out2 <- strategy2.getAction(input).transformS[RState](_._1, (t, i) => (i, t._2))
      runOut = (out1, out2)
      _ <- worldUpdate(runOut, in)
    } yield ()
}

//trait InterleavingStrategyRunner[In, Out, S] extends StrategyRunner[In, Out, S] {
//  def run(strategy1: Strategy[In, (In, Out), RunnerState], strategy2: Strategy[In, (In, Out), RunnerState], in: In): State[RunnerState, (In, RunOut)] =
//    for {
//      action1 <- strategy1.getAction(in)
//      (in1, out1) = action1
//      action2 <- strategy2.getAction(in1)
//      (in2, out2) = action2
//    } yield (in2, (out1, out2))
//}

trait WinDeclarer[Out] {
  type RunOut = (Out, Out)

  // TODO: refactor and clean this up a bit: cleanly handle multiple players/perspectives, get rid of the Int
  def winner(outs: RunOut, perspective: Int): RpsWinState
}