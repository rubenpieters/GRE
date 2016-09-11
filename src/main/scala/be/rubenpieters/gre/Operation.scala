package be.rubenpieters.gre

import java.util.UUID

import be.rubenpieters.utils.MathUtils

/**
  * Created by ruben on 29/08/2016.
  */
trait Operation {

  def applyOperation(entity: Entity): Entity
}

trait PropertyOverrideOperation extends Operation {
  def propertyName: String
  def newValue: Long

  override def applyOperation(entity: Entity): Entity = {
    entity.updatedProperties(propertyName, newValue)
  }
}

trait AddEffectOperation extends Operation {
  def effect: Effect
  def effectApplier: EntityId

  override def applyOperation(entity: Entity): Entity = {
    val uuid = UUID.randomUUID.toString
    entity.updatedEffects(uuid, (effectApplier, IdleEffect(effect)))
  }
}

case class SimpleAddEffectOperation(effect: Effect, effectApplier: EntityId) extends AddEffectOperation {
}

case class ConstantPropertyOverride(entityName: String, propertyName: String, newValue: Long) extends PropertyOverrideOperation

case class PlusPropertyOverride(entityResolver: EntityResolver,
                                entityName: String,
                                propertyName: String,
                                addValue: Long
                               ) extends PropertyOverrideOperation {
  lazy val newValue: Long = entityResolver.getEntityProperty(entityName, propertyName) + addValue

  override def toString: String = {
    s"PlusPropertyOverride(enNm: $entityName, prNm: $propertyName, av: $addValue, nv: $newValue)"
  }
}

case class MinusPropertyOverride(entityResolver: EntityResolver,
                                 entityName: String,
                                 propertyName: String,
                                 minusValue: Long
                                ) extends PropertyOverrideOperation {
  lazy val newValue: Long = entityResolver.getEntityProperty(entityName, propertyName) - minusValue

  override def toString: String = {
    s"MinusPropertyOverride(enNm: $entityName, prNm: $propertyName, minusv: $minusValue, nv: $newValue)"
  }
}

case class ClampedPlusPropertyOverride(entityResolver: EntityResolver,
                                       entityName: String,
                                       propertyName: String,
                                       addValue: Long,
                                       maxValue: Long
                                      ) extends PropertyOverrideOperation {
  require(addValue >= 0)
  lazy val oldValue = entityResolver.getEntityProperty(entityName, propertyName)
  lazy val newValue: Long = MathUtils.clampedPlus(oldValue, addValue, maxValue)

  override def toString: String = {
    s"ClampedPlusPropertyOverride(enNm: $entityName, prNm: $propertyName, av: $addValue, mv: $maxValue, nv: $newValue)"
  }
}

case class ClampedMinusPropertyOverride(entityResolver: EntityResolver,
                                        entityName: String,
                                        propertyName: String,
                                        minusValue: Long,
                                        minValue: Long
                                       ) extends PropertyOverrideOperation {
  require(minusValue >= 0)
  lazy val oldValue = entityResolver.getEntityProperty(entityName, propertyName)
  lazy val newValue: Long = MathUtils.clampedMinus(oldValue, minusValue, minValue)

  override def toString: String = {
    s"ClampedMinusPropertyOverride(enNm: $entityName, prNm: $propertyName, minusv: $minusValue, minv: $minValue, nv: $newValue)"
  }
}