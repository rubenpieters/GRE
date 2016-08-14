package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.RuleSet

/**
  * Created by rpieters on 14/08/2016.
  */
case class ImmutableEntity(
                            groupId: String,
                            uniqueId: String,
                            properties: Map[String, Long],
                            ruleSet: RuleSet
                          ) {
  val activeRule = ruleSet.activeRule.apply(uniqueId) _
  lazy val withIncrRuleCounter = ImmutableEntity(groupId, uniqueId, properties, ruleSet.withIncrRuleCounter)

  def getPropertyByName(propertyName: String): Long = {
    properties(propertyName)
  }

}
