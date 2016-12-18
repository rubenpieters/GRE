package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
object HundredPGame extends App {
  val cardField = CardField.default1Field
  val cardDiscard = CardDiscard(List())
  val cardDeck = CardDeck(List(AddXToField(1), AddXToField(2), AddXToField(3), AddXToField(4)))

  val (field, discard, deck) = handleTurn(cardField, cardDiscard, cardDeck)
    println(field)
    println(discard)
    println(deck)
    println("+-----------")
  val (field2, discard2, deck2) = handleTurn(field, discard, deck)
    println(field2)
    println(discard2)
    println(deck2)
    println("+-----------")

  def handleTurn(cardField: CardContainer, cardDiscard: CardDiscard, cardDeck: CardDeck) = {
    val (drawnCards, newDeck, newDiscard) = CardDeck.draw(cardDeck, cardDiscard, 3)
    val hand = CardHand(drawnCards)
    hand.cards.foldLeft((cardField: CardContainer, newDiscard, newDeck)){ case ((field, discard, deck), card) =>
      val (newField, newDiscard) = Card.playAndDiscard(card, field, discard)
      (newField, newDiscard, deck)
    }
  }
}
