package be.rubenpieters.gre.rules
import be.rubenpieters.gre.entity.{Entity, EntityManager}

import scala.util.Random

/**
  * Created by rpieters on 14/05/2016.
  */
class NullRule extends AbstractRule {
  override def apply(fromEntity: Entity, entityManager: EntityManager, rng: Random): String = {
    "NullRule: nothing happens"
  }
}
