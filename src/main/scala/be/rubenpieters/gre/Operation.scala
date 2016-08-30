package be.rubenpieters.gre

import be.rubenpieters.utils.MathUtils

/**
  * Created by ruben on 29/08/2016.
  */
trait Operation {
  def propertyName: String
  def newValue: Long

  def newProperties(oldProperties: Properties): Properties = {
    oldProperties + (propertyName -> newValue)
  }
}
case class ConstantPropertyOverride(entityName: String, propertyName: String, newValue: Long) extends Operation

case class PlusPropertyOverride(entityResolver: EntityResolver,
                                entityName: String,
                                propertyName: String,
                                addValue: Long
                               ) extends Operation {
  lazy val newValue: Long = entityResolver.getEntityProperty(entityName, propertyName) + addValue

  override def toString: String = {
    s"PlusPropertyOverride(enNm: $entityName, prNm: $propertyName, av: $addValue, nv: $newValue)"
  }
}

case class MinusPropertyOverride(entityResolver: EntityResolver,
                                 entityName: String,
                                 propertyName: String,
                                 minusValue: Long
                                ) extends Operation {
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
                                      ) extends Operation {
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
                                       ) extends Operation {
  require(minusValue >= 0)
  lazy val oldValue = entityResolver.getEntityProperty(entityName, propertyName)
  lazy val newValue: Long = MathUtils.clampedMinus(oldValue, minusValue, minValue)

  override def toString: String = {
    s"ClampedMinusPropertyOverride(enNm: $entityName, prNm: $propertyName, minusv: $minusValue, minv: $minValue, nv: $newValue)"
  }
}

