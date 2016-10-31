package be.rubenpieters.model

/**
  * Created by ruben on 31/10/2016.
  */
trait StrategyRunner[In, Out] {
  type RunOut = (Out, Out)

  def run(strategy1: Strategy[In, Out], strategy2: Strategy[In, Out], in: In): RunOut
}

trait SimultaneousStrategyRunner[In, Out] extends StrategyRunner[In, Out] {
  def run(strategy1: Strategy[In, Out], strategy2: Strategy[In, Out], in: In): RunOut =
    (strategy1.getAction(in), strategy2.getAction(in))
}

trait WinDeclarer[Out] {
  type RunOut = (Out, Out)

  def winner(outs: RunOut): Int
}
