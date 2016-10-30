package be.rubenpieters.model

/**
  * Created by rpieters on 30/10/2016.
  */
trait Strategy[I, O] {
  def getAction(input: I): O
}
