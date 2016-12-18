package be.rubenpieters.model.hundredp


/**
  * Created by ruben on 18/12/16.
  */
sealed trait Card {
  def apply(cardContainer: CardContainer): CardContainer
}

object Card {
  Function

  def ifNumberCard(func: NumberCard => NumberCard): Card => Card = card => card match {
    case numberCard @ NumberCard(_, _) => func(numberCard)
    case _ => card
  }

  def playAndDiscard(card: Card, cardContainer: CardContainer, cardDiscard: CardDiscard)
  : (CardContainer, CardDiscard) = {
    (card(cardContainer), CardDiscard(cardDiscard.cards :+ card))
  }
}

case class NumberCard(originalValue: Int, value: Int) extends Card {
  def apply(cardContainer: CardContainer): CardContainer =
    cardContainer
}

object NumberCard {
  def apply(originalValue: Int): NumberCard = NumberCard(originalValue, originalValue)
}

case class AddXToField(x: Int) extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(value = card.value + x)

  def apply(cardContainer: CardContainer): CardContainer = {
    cardContainer.map(Card.ifNumberCard(ncFunc))
  }
}

case class AddXOToField(x: Int) extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(originalValue = card.originalValue + x)

  def apply(cardContainer: CardContainer): CardContainer = {
    cardContainer.map(Card.ifNumberCard(ncFunc))
  }
}