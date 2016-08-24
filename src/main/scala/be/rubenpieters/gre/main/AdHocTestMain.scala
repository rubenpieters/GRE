package be.rubenpieters.gre.main

import be.rubenpieters.gre.endcondition.ZeroHpEndCondition
import be.rubenpieters.gre.engine.EngineRunner
import be.rubenpieters.gre.engine.examples.battlegame._
import be.rubenpieters.gre.entity.ImmutableEntity
import be.rubenpieters.gre.rules.RuleSet
import be.rubenpieters.gre.simulation.SimulationRunner


/**
  * Created by rpieters on 14/05/2016.
  */

object AdHocTestMain extends App {

  // 50-50 setup 1
  //  val entity1 = ImmutableEntity("G1", "E1", entityBasicProperties,
  //    RuleSet.initWithRepresentationAmount(Seq(
  //      (10, new AttackWithWeaponRule("E2"))
  //      ,(1, new EquipWeaponRule(Weapon(6, 12, 1, 1)))
  //      ,(10, new RegenFatigueRule())
  //      ,(18, new DisarmRule("E2"))
  //    )),
  //    Seq(new EquipWeaponRule(BattleGameEngine.baseWeapon))
  //  )
  //  val entity2 = ImmutableEntity("G2", "E2", entityBasicProperties,
  //    RuleSet.initWithRepresentationAmount(Seq(
  //      (2, new AttackWithWeaponRule("E1"))
  //      ,(1, new EquipWeaponRule(Weapon(20, 40, 4, 1)))
  //      ,(5, new RegenFatigueRule())
  //    )),
  //    Seq(new EquipWeaponRule(BattleGameEngine.baseWeapon))
  //  )

  val entity1 = ImmutableEntity("G1", "E1", BattleGameGeneralRuleSet.entityBasicProperties,
    RuleSet.initWithRepresentationAmount(Seq(
      (3 , new BattleGameGeneralRuleSet.GenerateResource(1, 1))
      ,(1, BattleGameRuleSet1.equipRuleSet1Weapon)
      ,(1, BattleGameGeneralRuleSet.swingWeaponWithFumbleRule("E2"))
    )),
    Seq(new BattleGameGeneralRuleSet.EquipWeaponRule(BattleGameGeneralRuleSet.baseWeapon))
  )
  val entity2 = ImmutableEntity("G2", "E2", BattleGameGeneralRuleSet.entityBasicProperties,
    RuleSet.initWithRepresentationAmount(Seq(
      (1, new BattleGameGeneralRuleSet.GenerateResource(1, 1))
      ,(1, BattleGameRuleSet2.equipRuleSet2Weapon)
      ,(1, BattleGameGeneralRuleSet.swingWeaponWithFumbleRule("E1"))
    )),
    Seq(new BattleGameGeneralRuleSet.EquipWeaponRule(BattleGameGeneralRuleSet.baseWeapon))
  )


  val runner = new EngineRunner(
    Seq(entity1, entity2)
    ,Set()
    ,Set(ZeroHpEndCondition)
  )

  runner.runUntilEndConditionReached()

  printableHistory(runner).foreach(println)

  val simulation = new SimulationRunner[(Long, Long)](
    Seq(entity1, entity2)
    ,Set()
    ,Set(ZeroHpEndCondition)
    ,(immutableEntityManager) => (immutableEntityManager.getEntityProperty("E1", "HP"), immutableEntityManager.getEntityProperty("E2", "HP"))
  )

  val simulationResults = simulation.runXSimulations(10000)
  println ("E1 losses")
  println(simulationResults.count{ x => x._1 <= 0})
  println ("E2 losses")
  println(simulationResults.count{ x => x._2 <= 0})


  def printableHistory(engineRunner: EngineRunner) = {
    engineRunner.entityManagerHistory.map { entityManager =>
      (
        entityManager.nextEntityId + 1
        ,entityManager.nextActiveRule.label
        ,entityManager.getEntityProperty("E1", "HP")
        ,entityManager.getEntityProperty("E1", "RESOURCE_1")
        ,entityManager.getEntityProperty("E1", "RESOURCE_2")
        ,entityManager.getEntityProperty("E2", "HP")
        ,entityManager.getEntityProperty("E2", "RESOURCE_1")
        ,entityManager.getEntityProperty("E2", "RESOURCE_2")
        )
    }
  }
}