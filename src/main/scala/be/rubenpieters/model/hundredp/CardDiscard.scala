package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
case class CardDiscard(cards: List[Card]) extends CardContainer[CardDiscard] {
  override def map(f: (Card) => Card): CardDiscard = CardDiscard(cards.map(f))
}
