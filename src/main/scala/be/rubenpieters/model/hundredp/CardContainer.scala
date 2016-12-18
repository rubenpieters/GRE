package be.rubenpieters.model.hundredp

import be.rubenpieters.model.hundredp.Card.Card

/**
  * Created by ruben on 18/12/16.
  */
trait CardContainer {
  def cards: List[Card]
}
