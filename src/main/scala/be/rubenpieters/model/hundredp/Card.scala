package be.rubenpieters.model.hundredp

import be.rubenpieters.model.hundredp.Card.Card


/**
  * Created by ruben on 18/12/16.
  */
object Card {
  type Card = CardContainer => CardContainer

  def ifNumberCard(func: NumberCard => NumberCard): Card => Card = card => card match {
    case numberCard @ NumberCard(_, _) => func(numberCard)
    case _ => card
  }
}

case class NumberCard(originalValue: Int, value: Int) extends Card {
  override def apply(cardContainer: CardContainer): CardContainer =
    cardContainer

  override def toString(): String = s"NumberCard($originalValue, $value)"
}

object NumberCard {
  def apply(originalValue: Int): NumberCard = NumberCard(originalValue, originalValue)
}

case class AddXToField(x: Int) extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(value = card.value + x)

  override def apply(cardContainer: CardContainer): CardContainer = {
    cardContainer.map(Card.ifNumberCard(ncFunc))
  }
}

case class AddXOToField(x: Int) extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(originalValue = card.originalValue + x)

  override def apply(cardContainer: CardContainer): CardContainer = {
    cardContainer.map(Card.ifNumberCard(ncFunc))
  }
}