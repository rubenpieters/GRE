package main.scala.be.rubenpieters.gre.engine.examples.blackjack

import be.rubenpieters.gre.endcondition.EndCondition
import be.rubenpieters.gre.engine.{EngineFactory, EngineRunner}
import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.log.{ConsolePrintListener, LogListener}
import be.rubenpieters.gre.rules.{AbstractRule, SinglePropertyOperationRule}

/**
  * Created by ruben on 12/07/2016.
  */
object SimpleBlackjackEngine {

  def cardHitRule(playerLabel: String, label: String) = {
    new SinglePropertyOperationRule(
      label,
      (hp, player, ruleEngineParameters) =>
      {
        val rng = ruleEngineParameters.rng
        val currentPlayerTotal = player.properties.get("CURRENT_TOTAL").get
        val cardValue = rng.nextInt(11)
        val newPlayerTotal = currentPlayerTotal + cardValue
        (newPlayerTotal,
          "'" + player.uniqueId + "' hits card " + cardValue + ", new total: " + newPlayerTotal
          )
      },
      playerLabel,
      "CURRENT_TOTAL"
    )
  }

  def dealerEntity(uniqueId: String): Entity = {
    new Entity("dealer", uniqueId,
      Map("CURRENT_TOTAL" -> 0),
      Seq(cardHitRule("dealer", "DEALER_HIT"))
    )
  }

  def playerEntity(uniqueId: String, strategy: Seq[AbstractRule]): Entity = {
    new Entity("player", uniqueId,
      Map("CURRENT_TOTAL" -> 0),
      strategy
    )
  }

  def createNewEntities(): Set[Entity] = {
    val dealer = dealerEntity("dealer")
    val player = playerEntity("player", Seq(cardHitRule("player", "PLAYER_HIT")))
    Set(dealer, player)
  }

  def simpleEngineWithLoggers(logListeners: Set[LogListener]): EngineRunner = {
    new EngineRunner(
      Seq("dealer", "player"),
      createNewEntities(),
      logListeners,
      Set(BustEndCondition),
      1L
    )
  }

  def simpleEngineFactory: EngineFactory = {
    new EngineFactory(
      Seq("dealer", "player"),
      createNewEntities,
      Set(ConsolePrintListener),
      Set(BustEndCondition)
    )
  }
}

object BustEndCondition extends EndCondition {
  override def checkCondition(engineRunner: EngineRunner): Option[String] = {
    val checkResult = engineRunner.entityManager.entityMap.exists { case (id, entity) =>
      entity.properties.get("CURRENT_TOTAL") match {
        case Some(total) if total >= 21 => true
        case _ => false
      }
    }
    if (checkResult) {
      Some("Engine ended because an entity busted")
    } else {
      None
    }
  }
}