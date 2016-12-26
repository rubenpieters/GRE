package be.rubenpieters.model.hundredp

import be.rubenpieters.util.ImmutableRng
import cats.data.State

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

  val play2 = for {
    shuffledP1 <- Player.shuffleDeck(player1)
    shuffledP2 <- Player.shuffleDeck(player2)
    players0 = List(shuffledP1, shuffledP2)
    _ = players0.foreach(println)
    draw1 <- Player.drawWithShuffle(shuffledP1, 3)
    (hand1, newPlayer1) = draw1
    players1 <- State.pure(handleTurn(players0.updated(0, newPlayer1), hand1, 0))
    _ = println("--------")
    _ = players1.foreach(println)
    draw2 <- Player.drawWithShuffle(players1(1), 3)
    (hand2, newPlayer2) = draw2
    players2 <- State.pure(handleTurn(players1.updated(1, newPlayer2), hand2, 1))
    _ = println("--------")
    _ = players2.foreach(println)
  } yield players2

  play2.run(ImmutableRng.scrambled(1)).value

  def handleTurn(players: List[Player], hand: CardHand, turnPlayer: Int): List[Player] = {
    hand.cards.foldLeft(players) { case (ps, card) =>
      val newPlayers = ps.map(p => p.copy(cardField = card(p.cardField)))
      val newPlayersAndDiscardedCard = newPlayers.updated(turnPlayer, Player.discard(newPlayers(turnPlayer), card))
      newPlayersAndDiscardedCard
    }
  }

  //  def playX(cardField: CardContainer, cardDiscard: CardDiscard, cardDeck: CardDeck, x: Int) = {
  //    List.iterate((cardField, cardDiscard, cardDeck), x)((handleTurn _).tupled)
  //  }
}
