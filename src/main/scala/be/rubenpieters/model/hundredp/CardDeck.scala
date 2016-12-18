package be.rubenpieters.model.hundredp

import be.rubenpieters.model.hundredp.Card.Card

/**
  * Created by ruben on 18/12/16.
  */
case class CardDeck(cards: List[Card]) extends CardContainer {
  override def create: (List[Card]) => CardContainer = CardDeck.apply
}
