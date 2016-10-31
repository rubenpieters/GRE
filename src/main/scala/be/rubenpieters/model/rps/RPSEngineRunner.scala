package be.rubenpieters.model.rps

import be.rubenpieters.model.rps._
import be.rubenpieters.model.{SimultaneousStrategyRunner, Strategy, StrategyRunner, WinDeclarer}

/**
  * Created by ruben on 31/10/16.
  */
object RpsEngineRunner {
  // TODO: generalize these functions
  // TODO: change/generalize output type
  def turnWinner(strategy1: Strategy[RpsInput, RpsOutput], strategy2: Strategy[RpsInput, RpsOutput]): Int = {
    val action1 = strategy1.getAction(())
    val action2 = strategy2.getAction(())
    actionCompare(action1, action2)
  }

  def actionCompare(action1: RpsOutput, action2: RpsOutput): Int = {
    1
  }
}

object RpsStrategyRunner extends SimultaneousStrategyRunner[RpsInput, RpsOutput]

//object RpsWinDeclarer extends WinDeclarer[RpsOutput] {
//  override def winner(outs: (RpsOutput, RpsOutput)): Int = {
//
//  }
//}