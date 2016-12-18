package be.rubenpieters.model.hundredp


import be.rubenpieters.model.hundredp.Card.Card
import cats.{Functor, Id, Traverse}

/**
  * Created by ruben on 18/12/16.
  */
trait CardContainer {
  def cards: List[Card]
  def create: List[Card] => CardContainer
  def map(f: Card => Card) = create(cards.map(f))
}