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
  val cardDeckP1 = CardDeck(List(AddXToField(1), AddXToField(2), AddXToField(3), AddXToField(4)))

  val player1 = Player(cardFieldP1, cardDiscardP1, cardDeckP1)

  val cardFieldP2 = CardField.default1Field
  val cardDiscardP2 = CardDiscard(List())
  val cardDeckP2 = CardDeck(List(AddXToField(5), AddXToField(6), AddXToField(7), AddXToField(8)))

  val player2 = Player(cardFieldP2, cardDiscardP2, cardDeckP2)

  //  playX(cardField, cardDiscard, cardDeck, 3).foreach(println)
  //  val (field, discard, deck) = handleTurn(cardField, cardDiscard, cardDeck)
  //    println(field)
  //    println(discard)
  //    println(deck)
  //    println("+-----------")
  //  val (field2, discard2, deck2) = handleTurn(field, discard, deck)
  //    println(field2)
  //    println(discard2)
  //    println(deck2)
  //    println("+-----------")

  val initialPlayers = List(player1, player2)

  val turnOrder = initialPlayers.indices.toList
  val turnOrders = List.fill(1)(turnOrder).flatten

  type StateRng[A] = State[ImmutableRng, A]

  val play = for {
    players0 <- initialPlayers.traverseU(p => Player.shuffleDeck(p))
    resultPlayers <- turnOrders.foldM[StateRng, List[Player]](players0){ case (players, turnPlayer) => handleTurn(players, turnPlayer)}
  } yield resultPlayers

  val result = play.run(ImmutableRng.scrambled(1)).value

  result._2.foreach(println)

  def handleTurn(players: List[Player], turnPlayer: Int): State[ImmutableRng, List[Player]] = for {
    draw <- Player.drawWithShuffle(players(turnPlayer), 3)
    (drawnHand, drawnPlayer) = draw
    newPlayers <- State.pure(playHand(players.updated(turnPlayer, drawnPlayer), drawnHand, turnPlayer))
  } yield newPlayers

  def playHand(players: List[Player], hand: CardHand, turnPlayer: Int): List[Player] = {
    hand.cards.foldLeft(players) { case (ps, card) =>
      val newPlayers = ps.map(p => p.copy(cardField = card(p.cardField)))
      val newPlayersAndDiscardedCard = newPlayers.updated(turnPlayer, Player.discard(newPlayers(turnPlayer), card))
      newPlayersAndDiscardedCard
    }
  }
}
