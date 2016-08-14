package be.rubenpieters.gre.simulation

import be.rubenpieters.gre.endcondition.EndCondition
import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.entity.{ImmutableEntity, ImmutableEntityManager}
import be.rubenpieters.gre.log.LogListener

/**
  * Created by rpieters on 19/06/2016.
  */
class SimulationRunner[T](
                           entities: Seq[ImmutableEntity],
                           logListeners: Set[LogListener],
                           endConditions: Set[EndCondition],
                           seed: Long,
                           simulationResult: ImmutableEntityManager => T
                         ) {
  def runXSimulations(x: Long): Seq[T] = {
    val result = (1L to x).map { i =>
      //println("SIMULATION " + i)
      val engine = new EngineRunner(
        entities,
        logListeners,
        endConditions,
        seed
      )
      engine.runUntilEndConditionReached()
      simulationResult.apply(engine.entityManager)
    }
    result
  }
}
