package be.rubenpieters.gre.rules

import java.util.UUID

/**
  * Created by rpieters on 7/08/2016.
  */
class SetPropertyRule(label: String = UUID.randomUUID().toString, entityName: String, propertyName: String, newPropertyValue: Long)
  extends SinglePropertyOperationRule(
    label,
    (oldPropertyValue, entity, parameters) => (newPropertyValue, s"Property $propertyName set to $newPropertyValue"),
    entityName,
    propertyName
  ) {

}
