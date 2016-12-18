package be.rubenpieters.model.hundredp

import be.rubenpieters.model.hundredp.Card.Card

/**
  * Created by ruben on 18/12/16.
  */
object Card {
  type Card = CardField => CardField
}

case class NumberCard(originalValue: Int, value: Int) extends Card {
  override def apply(cardField: CardField): CardField =
    cardField

  override def toString(): String = s"NumberCard($originalValue, $value)"
}

object NumberCard {
  def apply(originalValue: Int): NumberCard = NumberCard(originalValue, originalValue)
}

case class AddXToField(x: Int) extends Card {
  override def apply(cardField: CardField): CardField = {
    CardField(cardField.cards.map(card => card.copy(value = card.value + x)))
  }
}

case class AddXOToField(x: Int) extends Card {
  override def apply(cardField: CardField): CardField = {
    CardField(cardField.cards.map(card => card.copy(originalValue = card.originalValue + x)))
  }
}