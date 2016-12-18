package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
object HundredPGame extends App {
  val cardField = CardField.default1Field

  val addXCard = AddXToField(2)
  addXCard(cardField).cards.foreach(println)
}
