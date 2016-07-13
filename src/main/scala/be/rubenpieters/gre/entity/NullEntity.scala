package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.AbstractRule

/**
  * Created by rpieters on 14/05/2016.
  */
object NullEntity {
  val instance = new Entity("___RESERVED___NULL", "___RESERVED___NULL", Map(), Seq())


  def popRule(): AbstractRule = {
    throw new IllegalStateException("Popping rule of NullEntity")
  }
}
