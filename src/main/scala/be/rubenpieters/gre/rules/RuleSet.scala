package be.rubenpieters.gre.rules

import be.rubenpieters.gre.utils.MathUtils

import scala.util.Random

/**
  * Created by rpieters on 14/08/2016.
  */
case class RuleSet(ruleSeq: Seq[AbstractRule], currentRuleId: Int) {
  require(currentRuleId < ruleSeq.size)

  lazy val activeRule = ruleSeq(currentRuleId)
  lazy val nextRule = MathUtils.addOneWithWraparound(currentRuleId, ruleSeq.size)
  lazy val withIncrRuleCounter = RuleSet(ruleSeq, nextRule)

  def shuffled(rng: Random) = {
    RuleSet(rng.shuffle(ruleSeq), currentRuleId)
  }
}

object RuleSet {
  def init(ruleSeq: Seq[AbstractRule]): RuleSet = {
    initWithRepresentationAmount(ruleSeq.map{x => (1, x)})
  }

  def initWithRepresentationAmount(ruleSeqWithRepresentationAmount: Seq[(Int, AbstractRule)]): RuleSet = {
    ruleSeqWithRepresentationAmount match {
      case Seq() => throw new IllegalArgumentException("RuleSequence must have at least one rule to initialize")
      case _ =>
    }
    val flattenedRuleSeq = ruleSeqWithRepresentationAmount.flatMap { case (representationAmount, rule) =>
      Seq.fill(representationAmount)(rule)
    }
    RuleSet(flattenedRuleSeq, 0)
  }
}