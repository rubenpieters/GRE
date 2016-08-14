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
}
