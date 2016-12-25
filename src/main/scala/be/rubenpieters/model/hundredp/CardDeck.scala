package be.rubenpieters.model.hundredp

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
}