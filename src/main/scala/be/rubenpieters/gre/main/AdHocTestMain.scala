package be.rubenpieters.gre.main

import be.rubenpieters.gre.engine.examples.simplegame.SimpleGameEngine

/**
  * Created by rpieters on 14/05/2016.
  */
object AdHocTestMain extends App {
  val engineRunner = SimpleGameEngine.simpleEngine

  engineRunner.runUntilEndConditionReached()
}
