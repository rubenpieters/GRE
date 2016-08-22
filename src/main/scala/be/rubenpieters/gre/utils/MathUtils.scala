package be.rubenpieters.gre.utils

/**
  * Created by rpieters on 14/08/2016.
  */
object MathUtils {
  def addWithWraparound(value: Int, addValue: Int, wrapAroundValue: Int, resetValue: Int = 0): Int = {
    value + addValue match {
      case newValue if newValue >= wrapAroundValue => resetValue
      case newValue => newValue
    }
  }

  def addOneWithWraparound(value: Int, wrapAroundValue: Int, resetValue: Int = 0): Int = {
    addWithWraparound(value, 1, wrapAroundValue, resetValue)
  }

  def clampedPlus(oldValue: Long, addValue: Long, maxValue: Long): Long = {
    if (oldValue > maxValue - addValue) {
      maxValue
    } else {
      oldValue + addValue
    }
  }

  def clampedMinus(oldValue: Long, minusValue: Long, minValue: Long): Long = {
    if (oldValue < minValue + minusValue) {
      minValue
    } else {
      oldValue - minusValue
    }
  }
}
