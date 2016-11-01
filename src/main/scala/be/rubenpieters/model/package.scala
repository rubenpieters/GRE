package be.rubenpieters

import be.rubenpieters.util.ImmutableRng

/**
  * Created by ruben on 31/10/2016.
  */
package object model {
  type RunnerState = (ImmutableRng, (Int, Int, Int))
}
