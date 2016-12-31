package be.rubenpieters.model.hundredp

import be.rubenpieters.util.ImmutableRng
import cats.data.State
import cats._
import cats.data._
import cats.implicits._

/**
  * Created by ruben on 18/12/16.
  */
object HundredPGame extends App {
  val cardFieldP1 = CardField.default1Field
  val cardDiscardP1 = CardDiscard(List())
  val cardDeckP1 = CardDeck(List(AddXToField(1), AddXToField(1), AddXToField(1), AddXToField(1)))

  val player1 = Player(cardFieldP1, cardDiscardP1, cardDeckP1)

  val cardFieldP2 = CardField.default1Field
  val cardDiscardP2 = CardDiscard(List())
  val cardDeckP2 = CardDeck(List(AddXToField(1), AddXToField(1), AddXToField(1), AddXToField(1)))

  val player2 = Player(cardFieldP2, cardDiscardP2, cardDeckP2)

  val initialPlayers = List(player1, player2)

  val turnOrder = initialPlayers.indices.toList
  val turnOrders = List.fill(1000)(turnOrder).flatten

  type StateRng[A] = State[ImmutableRng, A]

  // either -playing- or -finished-
  type GameState = Either[List[Player], List[(Player, Boolean)]]

  val play = for {
    players0 <- initialPlayers.traverseU(p => Player.shuffleDeck(p))
    resultPlayers <- turnOrders.foldM[StateRng, GameState](Left(players0)){
      case (state, turnPlayer) => state match {
        // playing
        case Left(ps) => handleTurn(ps, turnPlayer)
        // finished
        case Right(l) => State.pure(Right(l))
      }
    }
  } yield resultPlayers

  val result = play.run(ImmutableRng.scrambled(1)).value

  println(result._2)

  def handleTurn(players: List[Player], turnPlayer: Int): State[ImmutableRng, GameState] = for {
    draw <- Player.drawWithShuffle(players(turnPlayer), 3)
    (drawnHand, drawnPlayer) = draw
    newPlayers <- State.pure(playHand(players.updated(turnPlayer, drawnPlayer), drawnHand, turnPlayer))
  } yield {
    val newPlayersWithEndCond = checkEndCondition(newPlayers)
    if (newPlayersWithEndCond.exists(_._2)) {
      Right(newPlayersWithEndCond): GameState
    } else {
      Left(newPlayers): GameState
    }}

  def playHand(players: List[Player], hand: CardHand, turnPlayer: Int): List[Player] = {
    hand.cards.foldLeft(players) { case (ps, card) =>
      val newPlayers = ps.map(card(_))
      val newPlayersAndDiscardedCard = newPlayers.updated(turnPlayer, Player.discard(newPlayers(turnPlayer), card))
      newPlayersAndDiscardedCard
    }
  }

  def checkEndCondition(players: List[Player]): List[(Player, Boolean)] = {
    players.map { p =>
      (p, p.cardField.cards.forall(_.value == 100))
    }
  }
}
