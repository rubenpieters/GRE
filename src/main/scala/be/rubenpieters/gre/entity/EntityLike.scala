package be.rubenpieters.gre.entity

import be.rubenpieters.gre.rules.AbstractRule

/**
  * Created by rpieters on 28/08/2016.
  */
trait EntityLike {
  def uniqueId: String
  def properties: Map[String, Long]

  def getPropertyByName(propertyName: String): Long = {
    properties(propertyName)
  }

  def apply(key: String): Long = properties.apply(key)
}

trait GlobalEffectEntityLike extends EntityLike {
  def effects: List[Effect]
}