package be.rubenpieters.gre.engine.examples.battlegame

import be.rubenpieters.gre.engine.examples.battlegame.BattleGameGeneralRuleSet.EquipWeaponRule
import be.rubenpieters.gre.entity.EntityResolver
import be.rubenpieters.gre.rules._

/**
  * Created by ruben on 24/08/2016.
  */
object BattleGameGeneralRuleSet {
  val entityBasicProperties: Map[String, Long] =
    Map(
      "HP" -> 100
      ,"MAXHP" -> 100
      ,"DAMAGE_RESIST_1" -> 0
      ,"DAMAGE_RESIST_2" -> 0
      ,"DAMAGE_RESIST_3" -> 0
      ,"DAMAGE_RESIST_4" -> 0
      ,"RESOURCE_1" -> 0
      ,"RESOURCE_2" -> 0
      ,"RESOURCE_3" -> 0
      ,"RESOURCE_4" -> 0
    )

  val baseWeapon = Weapon(1, 1, 0, 1)

  def swingWeaponWithFumbleRule(targetId: String) =
    IfElseFumbleRule((fromEntityId, entityResolver, parameters) =>
      entityResolver.getEntityProperty(fromEntityId, "RESOURCE_1") >
        entityResolver.getEntityProperty(fromEntityId, "WEAPON_FATIGUE_TURNS")
      , new SwingWeaponRule(targetId)
      , "SWING_WEAPON_FUMBLE"
    )

  class EquipWeaponRule(weapon: Weapon) extends DefaultRule {
    override def label = "EQUIP_WPN"

    override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
      Seq(
        ConstantPropertyOverride(fromEntityId, "WEAPON_MIN_ATK", weapon.minAtk)
        ,ConstantPropertyOverride(fromEntityId, "WEAPON_MAX_ATK", weapon.maxAtk)
        ,ConstantPropertyOverride(fromEntityId, "WEAPON_FATIGUE_TURNS", weapon.fatigueTurns)
        ,ConstantPropertyOverride(fromEntityId, "WEAPON_DAMAGE_TYPE", weapon.damageType)
      )
    }
  }

  class GenerateResource(resourceType: Long, amt: Long) extends DefaultRule {
    override def label = "GEN_RES_" + resourceType

    override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
      Seq(
        PlusPropertyOverride(entityResolver, fromEntityId, "RESOURCE_" + resourceType, amt)
      )
    }
  }
}

// sword, tanky type
object BattleGameRuleSet1 {
  val ruleSet1Weapon = Weapon(5, 10, 3, 1)

  val equipRuleSet1Weapon = new EquipWeaponRule(ruleSet1Weapon)

  // raise shield: increase defense/block attack
  // parry: next turn: riposte attack / if no attack self stun
  // bash lower defense next turn, decrease enemy resources
}

// dagger rogue type
object BattleGameRuleSet2 {
  val ruleSet2Weapon = Weapon(2, 4, 1, 2)

  val equipRuleSet2Weapon = new EquipWeaponRule(ruleSet2Weapon)

  // disarm
  // poison weapon, attacks have chance to deal extra damage
  // dodge: chance to dodge attack

}

// dagger witch doctor/hexer type
object BattleGameRuleSet3 {
  val ruleSet3Weapon = Weapon(3, 8, 2, 3)

  val equipRuleSet3Weapon = new EquipWeaponRule(ruleSet3Weapon)

  // dot
  // weaken damage
  // drain life
  // infested weapon: attacks have chance to infest (infest slows hp regen and deals low dot dmg)
  // detonate infestation: deal damage for all infestations
}

// mace, clericy type
object BattleGameRuleSet4 {
  val ruleSet4Weapon = Weapon(7, 14, 4, 4)

  val equipRuleSet4Weapon = new EquipWeaponRule(ruleSet4Weapon)

  // heal
  // buff damage
  // cleanse
  // meditate, increase resource generation, slightly increase dres/hp

}

// heavy sword, agressive soldier type
object BattleGameRuleSet5 {
  val ruleSet5Weapon = Weapon(10, 20, 5, 5)

  val equipRuleSet5Weapon = new EquipWeaponRule(ruleSet5Weapon)

  // stun, enemy skips rule
  // heavy attack, huge damage, skip next rule
  // enrage, lower health/res, increase atk

}