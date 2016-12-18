package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
object HundredPGame extends App {
  val cardField = CardField.default1Field
  val cardDeck = CardDeck(List(AddXToField(2), AddXToField(2), AddXToField(2), AddXToField(2)))
  val cardDiscard = CardDiscard(List())
  val (drawnCards, newDeck, newDiscard) = CardDeck.draw(cardDeck, cardDiscard, 3)

  val cardHand = CardHand(drawnCards)

//  cardHand.cards.head(cardField).cards.foreach(println)
  val result = cardHand.cards.foldLeft((cardField: CardContainer, newDiscard, newDeck)){ case ((field, discard, deck), card) =>
    val (newField, newDiscard) = Card.playAndDiscard(card, field, discard)
    (newField, newDiscard, deck)
  }
  println(result._1)
  println(result._2)
  println(result._3)
}
