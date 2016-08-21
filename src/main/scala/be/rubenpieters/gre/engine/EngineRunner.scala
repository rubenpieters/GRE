package be.rubenpieters.gre.engine

import be.rubenpieters.gre.endcondition.EndCondition
import be.rubenpieters.gre.entity.{ImmutableEntity, ImmutableEntityManager}
import be.rubenpieters.gre.log.LogListener
import be.rubenpieters.gre.rules.{AbstractRule, RuleEngineParameters}

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

  val entitiesWithShuffledRules = entities.map(_.withShuffledRules(rng))
  var entityManager = ImmutableEntityManager.entityManagerInit(entitiesWithShuffledRules)
  runInitialization()

  var endConditionReached = false

  val ruleEngineParameters = RuleEngineParameters(rng)

  def verifyRuleSetCosts() = {

  }

  def runInitialization() = {
    entities.foreach { entity =>
      entity.initializationRules.foreach { rule =>
        entityManager = entityManager.applyRule(rule(entity.uniqueId))
      }
    }
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
    // TODO: create a custom way to stop infinite loops/really long runs (sudden death activation after certain point?)
    val maxIterations = 1000
    var i = 0
    while (i <= maxIterations && ! endConditionReached) {
      runStep()
      i += 0
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