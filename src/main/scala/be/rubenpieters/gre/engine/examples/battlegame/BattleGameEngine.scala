package be.rubenpieters.gre.engine.examples.battlegame

import be.rubenpieters.gre.entity.EntityResolver
import be.rubenpieters.gre.rules._
import be.rubenpieters.gre.utils.RngUtils

/**
  * Created by rpieters on 7/08/2016.
  */
object BattleGameEngine {


}

case class Weapon(minAtk: Int, maxAtk: Int, fatigueTurns: Int)


class EquipWeaponRule(weapon: Weapon) extends DefaultRule {
  override def label = "EQUIP_WPN"

  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    Seq(
      ConstantPropertyOverride(fromEntityId, "WEAPON_MIN_ATK", weapon.minAtk)
      ,ConstantPropertyOverride(fromEntityId, "WEAPON_MAX_ATK", weapon.maxAtk)
      ,ConstantPropertyOverride(fromEntityId, "WEAPON_FATIGUE_TURNS", weapon.fatigueTurns)
    )
  }
}

class AttackWithWeaponRule(targetId: String) extends DefaultRule {
  override def label = "ATTACK_WPN"

  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver,
                               ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    val currentFatigueTurns = entityResolver.getEntityProperty(fromEntityId, "FATIGUE_TURNS")
    if (currentFatigueTurns == 0) {
      // Fatigue Turns is zero -> able to attack
      val weaponMinAtk = entityResolver.getEntityProperty(fromEntityId, "WEAPON_MIN_ATK")
      val weaponMaxAtk = entityResolver.getEntityProperty(fromEntityId, "WEAPON_MAX_ATK")
      val weaponFatigueTurns = entityResolver.getEntityProperty(fromEntityId, "WEAPON_FATIGUE_TURNS")
      // TODO: use a randomLongFromTo method
      val weaponAtkValue = RngUtils.randomIntFromTo(weaponMinAtk.toInt, weaponMaxAtk.toInt, ruleEngineParameters.rng).toLong
      Seq(
        PlusPropertyOverride(entityResolver, targetId, "HP", - weaponAtkValue)
        ,ConstantPropertyOverride(fromEntityId, "FATIGUE_TURNS", weaponFatigueTurns)
      )
    } else {
      // Fatigue Turns is not zero -> fumble attack, nothing happens
      Seq()
    }
  }
}

class RegenFatigueRule extends DefaultRule {
  override def label = "REGEN_FATIGUE"

  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver,
                               ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    Seq(
      ClampedMinusPropertyOverride(entityResolver, fromEntityId, "FATIGUE_TURNS", 1, 0)
    )
  }
}

class HealRule(amt: Long) extends DefaultRule {
  override def label = "HEAL"

  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    Seq(
      ClampedPlusPropertyOverride(entityResolver, fromEntityId, "HP", amt, entityResolver.getEntityProperty(fromEntityId, "MAXHP"))
    )
  }
}