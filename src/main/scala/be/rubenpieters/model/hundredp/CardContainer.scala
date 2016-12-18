package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
trait CardContainer {
  def cards: List[Card]
  def create: List[Card] => CardContainer
  def map(f: Card => Card) = create(cards.map(f))
}