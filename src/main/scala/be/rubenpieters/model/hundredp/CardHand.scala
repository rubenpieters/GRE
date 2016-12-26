package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
case class CardHand(cards: List[Card]) extends CardContainer[CardHand] {
  override def map(f: (Card) => Card): CardHand = CardHand(cards.map(f))
}

