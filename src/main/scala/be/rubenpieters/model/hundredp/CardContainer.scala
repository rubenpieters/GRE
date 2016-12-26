package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
trait CardContainer[SELF <: CardContainer[SELF]] {
  def cards: List[Card]
  def map(f: Card => Card): SELF
}