package be.rubenpieters.model.hundredp

import be.rubenpieters.util.ImmutableRng
import cats.data.State

/**
  * Created by ruben on 26/12/16.
  */
case class Player(cardField: CardField, cardDiscard: CardDiscard, cardDeck: CardDeck) {

}

object Player {
  def discard(player: Player, card: Card): Player = {
    player.copy(cardDiscard = CardDiscard(player.cardDiscard.cards :+ card))
  }

  def shuffleDeck(player: Player): State[ImmutableRng, Player] =
    for (shuffledDeck <- CardDeck.shuffle(player.cardDeck))
      yield player.copy(cardDeck = shuffledDeck)

  def drawWithShuffle(player: Player, x: Int): State[ImmutableRng, (CardHand, Player)] =
    for {
      r <- CardDeck.drawWithShuffle(player.cardDeck, player.cardDiscard, x)
      (drawnCards, newDeck, newDiscard) = r
    } yield (CardHand(drawnCards), Player(player.cardField, newDiscard, newDeck))
}