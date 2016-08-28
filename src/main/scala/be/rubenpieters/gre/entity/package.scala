package be.rubenpieters.gre

import be.rubenpieters.gre.rules.AbstractRule

/**
  * Created by rpieters on 28/08/2016.
  */
package object entity {
  type Effect = (String, AbstractRule, AbstractRule, Int)
}
