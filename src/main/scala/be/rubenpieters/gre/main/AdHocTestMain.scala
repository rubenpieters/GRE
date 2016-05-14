package be.rubenpieters.gre.main

import be.rubenpieters.gre.engine.EngineFactory

/**
  * Created by rpieters on 14/05/2016.
  */
object AdHocTestMain extends App {
  val engineRunner = EngineFactory.simpleEngine

  engineRunner.runStep()
  engineRunner.runStep()
  engineRunner.runStep()
  engineRunner.runStep()
}
