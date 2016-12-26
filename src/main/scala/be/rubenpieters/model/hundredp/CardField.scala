package be.rubenpieters.model.hundredp

/**
  * Created by ruben on 18/12/16.
  */
case class CardField(cards: List[NumberCard]) extends CardContainer[CardField] {
  override def map(f: (Card) => Card): CardField = {
    val filtered = cards.flatMap(card => card match {
      case nc@NumberCard(_, _) => {
        val transformed = f(nc: Card)
        transformed match {
          case nct@NumberCard(_,_) => Option(nct)
          case _ => None
        }
      }
      case _ => None
    })
    CardField(filtered)
  }
}

object CardField {
  def default0Field = CardField(List.fill(10)(NumberCard(0)))
  def default1Field = CardField(List.fill(10)(NumberCard(1)))
}