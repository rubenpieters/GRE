package be.rubenpieters

/**
  * Created by ruben on 29/08/2016.
  */
abstract class AbstractRule {
  def createOperations(): Seq[Operation] = Seq()
  def createEffects(): Seq[Effect] = Seq()
}

case class Operation() {

}

case class Effect() {

}
