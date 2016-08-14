package be.rubenpieters.gre.engine

import be.rubenpieters.gre.endcondition.EndCondition
import be.rubenpieters.gre.entity.{ImmutableEntity, ImmutableEntityManager}
import be.rubenpieters.gre.log.LogListener
import be.rubenpieters.gre.rules.RuleEngineParameters

import scala.util.Random

/**
  * Created by rpieters on 14/05/2016.
  */
class EngineRunner(
                    entities: Seq[ImmutableEntity],
                    logListeners: Set[LogListener],
                    endConditions: Set[EndCondition],
                    seed: Long = System.currentTimeMillis()
                  ) {
  verifyRuleSetCosts()

  val rng = new Random(seed)

  var entityManager = ImmutableEntityManager.entityManagerInit(entities)

  var endConditionReached = false

  val ruleEngineParameters = RuleEngineParameters(entityManager, rng)

  def verifyRuleSetCosts() = {

  }

  def runStep() = {
    if (! endConditionReached) {
      entityManager = entityManager.nextState
      checkEndConditions()
    } else {
      log("End condition reached")
    }
  }

  def runUntilEndConditionReached() = {
    while (! endConditionReached) {
      runStep()
    }
  }

  def checkEndConditions() = {
    val conditionChecks = endConditions
      .flatMap(_.checkCondition(this))
    if (conditionChecks.nonEmpty) {
      endConditionReached = true
      conditionChecks.foreach(log)
    }
  }

  def log(line: String) = {
    logListeners.foreach(_.log(line))
  }
}