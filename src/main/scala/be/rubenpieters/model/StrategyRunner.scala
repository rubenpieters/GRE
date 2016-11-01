package be.rubenpieters.model

import be.rubenpieters.model.rps.RpsWinState
import cats.data.State

/**
  * Created by ruben on 31/10/2016.
  */
trait StrategyRunner[In, Out, RState] {
  type RunOut = (Out, Out)

  def run(strategy1: Strategy[In, Out, RState], strategy2: Strategy[In, Out, RState], in: In): State[RState, RunOut]
}

// TODO: the simultaneous is basically a sort of applicative strategy and the interleaving is a sort of monadic strategy
// maybe should try to find some way to make use of this and make this look nicer
trait SimultaneousStrategyRunner[In, Out] extends StrategyRunner[In, Out, RunnerState] {
  def run(strategy1: Strategy[In, Out, RunnerState], strategy2: Strategy[In, Out, RunnerState], in: In): State[RunnerState, RunOut] =
    for {
      out1 <- strategy1.getAction(in)
      out2 <- strategy2.getAction(in)
    } yield (out1, out2)
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
