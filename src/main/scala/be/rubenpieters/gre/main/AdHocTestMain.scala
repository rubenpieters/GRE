package be.rubenpieters.gre.main

import be.rubenpieters.gre.endcondition.ZeroHpEndCondition
import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.engine.examples.battlegame.{AttackWithWeaponRule, EquipWeaponRule, Weapon}
import be.rubenpieters.gre.entity.ImmutableEntity
import be.rubenpieters.gre.log.ConsolePrintListener
import be.rubenpieters.gre.rules.RuleSet


/**
  * Created by rpieters on 14/05/2016.
  */

object AdHocTestMain extends App {
  val entity1 = ImmutableEntity("G1", "E1", Map("HP" -> 100),
    RuleSet.init(Seq(
      new EquipWeaponRule(Weapon(1, 10, 1)),
      new AttackWithWeaponRule("E2")
    )))
  val entity2 = ImmutableEntity("G2", "E2", Map("HP" -> 100),
    RuleSet.init(Seq(
      new EquipWeaponRule(Weapon(5, 5, 1)),
      new AttackWithWeaponRule("E1")
    )))

  val runner = new EngineRunner(
    Seq(entity1, entity2)
    ,Set(ConsolePrintListener)
    ,Set(ZeroHpEndCondition)
  )

  runner.runUntilEndConditionReached()

  println(runner.entityManager)
}