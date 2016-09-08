package be.rubenpieters.gre

/**
  * Created by ruben on 1/09/2016.
  */
class EntityRunner(entity: Entity) {



}

object EntityRunner extends App {
  val entity1 = Entity(
    "p1"
    ,Map("x" -> 0, "y" -> 0, "INITIATIVE"-> 1)
  ,Map()
  ,Map()
  ,RuleEngineParameters.newParameters
  ,CyclicRuleStrategy(
      Seq(
        new AbstractRule {
          override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
            Seq(
              ("p1", PlusPropertyOverride(entityResolver, "p1", "x", 1))
            )
          }
        },
        new AbstractRule {
          override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
            Seq(
              ("p1", PlusPropertyOverride(entityResolver, "p1", "y", 1))
            )
          }
        }
      )
    )
  )


  val entity2 = Entity(
    "p2"
    ,Map("z" -> 0, "INITIATIVE"-> 2)
    ,Map()
    ,Map()
    ,RuleEngineParameters.newParameters
    ,CyclicRuleStrategy(
      Seq(
        new AbstractRule {
          override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
            Seq(
              ("p2", PlusPropertyOverride(entityResolver, "p2", "z", 1))
            )
          }
        }
      )
    )
  )

  val scopeEntity = Entity(
    "scope"
    ,Map()
    ,Map("p1" -> entity1, "p1" -> entity2)
    ,Map()
    ,RuleEngineParameters.newParameters
    ,null
  )

  val entityStream = Stream.iterate(scopeEntity){
    entity =>
      entity.advance.asInstanceOf[Entity]
  }
  println("-- stream")
  entityStream.take(5).foreach(println)

  /*val rule = entity.ruleAdvanceStrategy.rule
  val newEntity = entity.applyRule(rule, "p1")
  println(newEntity)

  val entityStream = Stream.iterate(entity){ entity =>
    val (entityWithPoppedRule, rule) = entity.popRule
    entityWithPoppedRule.applyRule(rule, "p1")
  }
  println("-- stream")
  entityStream.take(5).foreach(println)

  def advance(entity: Entity): Entity = {
    val rule = entity.ruleAdvanceStrategy.rule
    entity.applyRule(rule, entity.id)
  }

  def test(i: Int ): Int = {
    println(s"i $i")
    i+1
  }*/
}