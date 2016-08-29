package be.rubenpieters

/**
  * Created by ruben on 29/08/2016.
  */
case class Entity(
                  id: String
                  ,properties: Map[String, Long] = Map()
                  ,subEntities: Map[String, Entity] = Map()
                 ) extends Identifiable {
  require(! subEntities.keys.exists(_.equals(id)))

  def getProperty(property: String): Long = {
    properties(property)
  }

  def applyRule(rule: AbstractRule, actingEntity: String, ruleEngineParameters: RuleEngineParameters): Entity = {
    this
  }

  def getEntity(findId: String): Entity = {
    id.equals(findId) match {
      case true => this
      case false => subEntities.get(findId) match {
        case Some(e) => e
        case None => throw new IllegalArgumentException(s"Entity $id does not contain an entity with id $findId")
      }
    }
  }

}

trait Identifiable {
  def id: String
}


