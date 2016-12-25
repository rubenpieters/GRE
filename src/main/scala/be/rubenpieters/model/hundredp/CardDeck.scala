package be.rubenpieters.model.hundredp

import be.rubenpieters.util.ImmutableRng
import cats.data.State

/**
  * Created by ruben on 18/12/16.
  */
case class CardDeck(cards: List[Card]) extends CardContainer {
  override def create: (List[Card]) => CardContainer = CardDeck.apply
}

object CardDeck {
  def draw(deck: CardDeck, discard: CardDiscard, x: Int): (List[Card], CardDeck, CardDiscard) = {
    require(x <= deck.cards.size + discard.cards.size)
    if (deck.cards.size >= x) {
      (deck.cards.take(x), CardDeck(deck.cards.drop(x)), discard)
    } else {
      val reshuffledDeck = CardDeck(deck.cards ++ discard.cards)
      draw(reshuffledDeck, CardDiscard(List()), x)
    }
  }

  def drawWithShuffle(deck: CardDeck, discard: CardDiscard, x: Int): State[ImmutableRng, (List[Card], CardDeck, CardDiscard)] = {
    require(x <= deck.cards.size + discard.cards.size)
    if (deck.cards.size >= x) {
      State.pure((deck.cards.take(x), CardDeck(deck.cards.drop(x)), discard))
    } else {
      for (reshuffledDeck <- shuffle(CardDeck(deck.cards ++ discard.cards)))
        yield draw(reshuffledDeck, CardDiscard(List()), x)
    }
  }

  def shuffle(deck: CardDeck): State[ImmutableRng, CardDeck] = for {
    shuffled <- ImmutableRng.shuffle(deck.cards)
  } yield CardDeck(shuffled)
}