package be.rubenpieters.gre.main

import be.rubenpieters.gre.endcondition.ZeroHpEndCondition
import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.engine.examples.battlegame.{AttackWithWeaponRule, EquipWeaponRule, HealRule, Weapon}
import be.rubenpieters.gre.entity.ImmutableEntity
import be.rubenpieters.gre.rules.RuleSet
import be.rubenpieters.gre.simulation.SimulationRunner


/**
  * Created by rpieters on 14/05/2016.
  */

object AdHocTestMain extends App {
  val entity1 = ImmutableEntity("G1", "E1", Map("HP" -> 30, "MAXHP" -> 30),
    RuleSet.init(Seq(
      new AttackWithWeaponRule("E2")
    )),
    Seq(new EquipWeaponRule(Weapon(10, 10, 1)))
  )
  val entity2 = ImmutableEntity("G2", "E2", Map("HP" -> 30, "MAXHP" -> 30),
    RuleSet.initWithRepresentationAmount(Seq(
      (1, new AttackWithWeaponRule("E1"))
      ,(3, new HealRule(20))
    )),
    Seq(new EquipWeaponRule(Weapon(10, 10, 1)))
  )

  val runner = new EngineRunner(
    Seq(entity1, entity2)
    ,Set()
    ,Set(ZeroHpEndCondition)
  )

  runner.runUntilEndConditionReached()

  println(runner.entityManager)

  /*val simulation = new SimulationRunner[(Long, Long)](
    Seq(entity1, entity2)
    ,Set()
    ,Set(ZeroHpEndCondition)
    ,(immutableEntityManager) => (immutableEntityManager.getEntityProperty("E1", "HP"), immutableEntityManager.getEntityProperty("E2", "HP"))
  )

  val simulationResults = simulation.runXSimulations(10000)
  println ("E1 losses")
  println(simulationResults.count{ x => x._1 <= 0})
  println ("E2 losses")
  println(simulationResults.count{ x => x._2 <= 0})*/
}