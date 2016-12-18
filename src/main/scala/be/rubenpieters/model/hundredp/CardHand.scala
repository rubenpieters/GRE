package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
case class CardHand(cards: List[Card]) extends CardContainer {
  override def create: (List[Card]) => CardContainer = CardHand.apply
}

