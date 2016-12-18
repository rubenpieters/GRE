package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
case class CardField(cards: List[NumberCard]) extends CardContainer {
  override def create: (List[Card]) => CardContainer = list => {
    val filtered = list.flatMap(card => card match {
      case nc @ NumberCard(_, _) => Option(nc)
      case _ => None
    })
    CardField.apply(filtered)
  }
}

object CardField {
  def default0Field = CardField(List.fill(10)(NumberCard(0)))
  def default1Field = CardField(List.fill(10)(NumberCard(1)))
}