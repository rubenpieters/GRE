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

case class AddXOToHand(x: Int) extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(originalValue = card.originalValue + x, value = card.value + x)

  override def apply(player: Player): Player = {
    player.copy(cardHand = player.cardHand.map(Card.ifNumberCard(ncFunc)))
  }
}

case object AverageField extends Card {
  def ncFunc(avg: Int): NumberCard => NumberCard = card => card.copy(value = avg)

  override def apply(player: Player): Player = {
    val avg = player.cardField.cards.foldLeft(0){ case (acc, card) => card.value + acc} / 10
    player.copy(cardField = player.cardField.map(Card.ifNumberCard(ncFunc(avg))))
  }
}

case object ReplaceLowest extends Card {
  override def apply(player: Player) = {
    implicit val ncOrdering = new Ordering[NumberCard] {
      override def compare(x: NumberCard, y: NumberCard): Int = Ordering[Int].compare(x.value, y.value)
    }
    val lowestFieldCardIndex = player.cardField.cards.zipWithIndex.min._2
    val lowestFieldCard = player.cardField.cards.min
    val handNumberCards = player.cardHand.cards.flatMap{
      case numberCard @ NumberCard(_, _) => Option(numberCard)
      case _ => None
    }
    if (handNumberCards.nonEmpty) {
      val lowestHandCardIndex = handNumberCards.zipWithIndex.min._2
      val lowestHandCard = handNumberCards.min

      player.copy(cardField = CardField(player.cardField.cards.updated(lowestFieldCardIndex, lowestHandCard))
        , cardHand = CardHand(player.cardHand.cards.updated(lowestHandCardIndex, lowestFieldCard)))
    } else {
      player
    }
  }
}

case object ResetToOriginal extends Card {
  val ncFunc: NumberCard => NumberCard = card => card.copy(value = card.originalValue)

  override def apply(player: Player): Player = {
    player.copy(cardHand = player.cardHand.map(Card.ifNumberCard(ncFunc)))
  }
}