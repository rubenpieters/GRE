package be.rubenpieters.model

/**
  * Created by ruben on 1/11/2016.
  */
trait MatchRunner[In, Out, RState] {
  def runX(strategy1: Strategy[In, Out, RState]
           , strategy2: Strategy[In, Out, RState]
           , in: In
           , initial: RState
           , roundRunner: RoundRunner[In, Out, RState]
           , x: Int
          ) = {
    def updateState(rState: RState): RState = {
      roundRunner.run(strategy1, strategy2, in).run(rState).value._1
    }

    List.iterate(initial, x)(updateState)
  }
}
