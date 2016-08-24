package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.{AbstractRule, RuleEngineParameters, RuleSet}

import scala.util.Random

/**
  * Created by rpieters on 14/08/2016.
  */
case class ImmutableEntity(
                            groupId: String,
                            uniqueId: String,
                            properties: Map[String, Long],
                            ruleSet: RuleSet,
                            initializationRules: Seq[AbstractRule] = Seq()
                          ) {
  val activeRule = ruleSet.activeRule
  val activeAppliedRule: (ImmutableEntityManager, RuleEngineParameters) => Map[String, ImmutableEntity] = activeRule.apply(uniqueId) _
  lazy val withIncrRuleCounter = ImmutableEntity(groupId, uniqueId, properties, ruleSet.withIncrRuleCounter)

  def getPropertyByName(propertyName: String): Long = {
    properties(propertyName)
  }

  def withShuffledRules(rng: Random): ImmutableEntity = {
    ImmutableEntity(groupId, uniqueId, properties, ruleSet.shuffled(rng), initializationRules)
  }

}
