package be.rubenpieters.gre.rules

import be.rubenpieters.gre.entity.{Entity, EntityManager}

/**
  * Created by rpieters on 14/05/2016.
  */
abstract class AbstractRule {
  def apply(fromEntity: Entity, entityManager: EntityManager): String
}
