package be.rubenpieters.model.hundredp

import be.rubenpieters.util.ImmutableRng

/**
  * Created by ruben on 18/12/16.
  */
object HundredPGame extends App {
  val cardField = CardField.default1Field
  val cardDiscard = CardDiscard(List())
  val cardDeck = CardDeck(List(AddXToField(1), AddXToField(2), AddXToField(3), AddXToField(4)))

  //  playX(cardField, cardDiscard, cardDeck, 3).foreach(println)
  //  val (field, discard, deck) = handleTurn(cardField, cardDiscard, cardDeck)
  //    println(field)
  //    println(discard)
  //    println(deck)
  //    println("+-----------")
  //  val (field2, discard2, deck2) = handleTurn(field, discard, deck)
  //    println(field2)
  //    println(discard2)
  //    println(deck2)
  //    println("+-----------")

  val play2 = for {
    t1 <- handleTurn(cardField, cardDiscard, cardDeck)
    (cardField1, cardDiscard1, cardDeck1) = t1
    _ = println(t1)
    t2 <- handleTurn(cardField1, cardDiscard1, cardDeck1)
    (cardField2, cardDiscard2, cardDeck2) = t2
    _ = println(t2)
  } yield t2

  play2.run(ImmutableRng.scrambled(1)).value

  def handleTurn(cardField: CardContainer, cardDiscard: CardDiscard, cardDeck: CardDeck) = {
    for {
      s <- CardDeck.drawWithShuffle(cardDeck, cardDiscard, 3)
      (drawnCards, newDeck, newDiscard) = s
    } yield {
      val hand = CardHand(drawnCards)
      hand.cards.foldLeft((cardField: CardContainer, newDiscard, newDeck)) { case ((field, discard, deck), card) =>
        val (newField, newDiscard) = Card.playAndDiscard(card, field, discard)
        (newField, newDiscard, deck)
      }
    }
  }

  //  def playX(cardField: CardContainer, cardDiscard: CardDiscard, cardDeck: CardDeck, x: Int) = {
  //    List.iterate((cardField, cardDiscard, cardDeck), x)((handleTurn _).tupled)
  //  }
}
