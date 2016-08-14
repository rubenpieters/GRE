package be.rubenpieters.gre.rules

import be.rubenpieters.gre.utils.MathUtils

/**
  * Created by rpieters on 14/08/2016.
  */
case class RuleSet(ruleSeq: Seq[AbstractRule], currentRuleId: Int) {
  require(currentRuleId < ruleSeq.size)

  val activeRule = ruleSeq(currentRuleId)
  lazy val nextRule = MathUtils.addOneWithWraparound(currentRuleId, ruleSeq.size)
  lazy val withIncrRuleCounter = RuleSet(ruleSeq, nextRule)
}

object RuleSet {
  def init(ruleSeq: Seq[AbstractRule]): RuleSet = {
    ruleSeq match {
      case Seq() => throw new IllegalArgumentException("RuleSequence must have at least one rule to initialize")
      case _ =>
    }
    RuleSet(ruleSeq, 0)
  }
}