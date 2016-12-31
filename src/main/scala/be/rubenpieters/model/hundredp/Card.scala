package be.rubenpieters.model.hundredp


/**
  * Created by ruben on 18/12/16.
  */
sealed trait Card {
  def apply(player: Player): Player
}

object Card {
  Function

  def ifNumberCard(func: NumberCard => NumberCard): Card => Card = card => card match {
    case numberCard @ NumberCard(_, _) => func(numberCard)
    case _ => card
  }

  def playAndDiscard(card: Card, player: Player, cardDiscard: CardDiscard)
  : (Player, CardDiscard) = {
    (card(player), CardDiscard(cardDiscard.cards :+ card))
  }
}

case class NumberCard(originalValue: Int, value: Int) extends Card {
  override def apply(player: Player): Player =
    player
}

object NumberCard {
  def apply(originalValue: Int): NumberCard = NumberCard(originalValue, originalValue)
}

case class AddXToField(x: Int) extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(value = card.value + x)

  override def apply(player: Player): Player = {
    player.copy(cardField = player.cardField.map(Card.ifNumberCard(ncFunc)))
  }
}

case class AddXOToField(x: Int) extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(originalValue = card.originalValue + x)

  override def apply(player: Player): Player = {
    player.copy(cardField = player.cardField.map(Card.ifNumberCard(ncFunc)))
  }
}
