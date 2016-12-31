package be.rubenpieters.model.hundredp

import be.rubenpieters.util.ImmutableRng
import cats.data.State

/**
  * Created by ruben on 26/12/16.
  */
case class Player(cardField: CardField, cardDiscard: CardDiscard, cardDeck: CardDeck, cardHand: CardHand) {

}

object Player {
  def discard(player: Player, card: Card, i: Int): Player = {
    player.copy(cardHand = CardHand(player.cardHand.cards.patch(i, Nil, 1)), cardDiscard = CardDiscard(player.cardDiscard.cards :+ card))
  }

  def shuffleDeck(player: Player): State[ImmutableRng, Player] =
    for (shuffledDeck <- CardDeck.shuffle(player.cardDeck))
      yield player.copy(cardDeck = shuffledDeck)

  def drawWithShuffle(player: Player, x: Int): State[ImmutableRng, Player] =
    for {
      r <- CardDeck.drawWithShuffle(player.cardDeck, player.cardDiscard, x)
      (drawnCards, newDeck, newDiscard) = r
    } yield Player(player.cardField, newDiscard, newDeck, CardHand(player.cardHand.cards ++ drawnCards))
}