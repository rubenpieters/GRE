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

  def getPropertyByName(propertyName: String): Long = {
    properties(propertyName)
  }
}
