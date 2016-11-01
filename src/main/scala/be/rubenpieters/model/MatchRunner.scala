package be.rubenpieters.model

/**
  * Created by ruben on 1/11/2016.
  */
trait MatchRunner[In, Out, VisibleState, InvisibleState] {
  def runX(strategy1: Strategy[In, Out, VisibleState]
           , strategy2: Strategy[In, Out, VisibleState]
           , in: In
           , initial: (VisibleState, InvisibleState)
           , roundRunner: RoundRunner[In, Out, VisibleState, InvisibleState]
           , x: Int
          ) = {
    type RState = (VisibleState, InvisibleState)

    def updateState(rState: RState): RState = {
      roundRunner.run(strategy1, strategy2, in).run(rState).value._1
    }

    List.iterate(initial, x)(updateState)
  }
}
