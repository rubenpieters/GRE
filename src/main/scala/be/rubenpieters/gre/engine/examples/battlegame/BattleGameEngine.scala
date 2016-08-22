package be.rubenpieters.gre.engine.examples.battlegame

import be.rubenpieters.gre.entity.EntityResolver
import be.rubenpieters.gre.rules._
import be.rubenpieters.gre.utils.{MathUtils, RngUtils}

/**
  * Created by rpieters on 7/08/2016.
  */
object BattleGameEngine {
  val baseWeapon = Weapon(1, 1, 0, 1)

}

case class Weapon(minAtk: Int, maxAtk: Int, fatigueTurns: Int, damageType: Int)

// should maybe use enumeration for damage types, need to step away from long values then
object DamageType extends Enumeration {
  val DT1 = Value("DT1")
  val DT2 = Value("DT2")
  val DT3 = Value("DT3")
  val DT4 = Value("DT4")
}

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

class DisarmRule(targetId: String) extends DefaultRule {
  override def label = "DISARM"

  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver,
                               ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    Seq(
      ConstantPropertyOverride(targetId, "WEAPON_MIN_ATK", BattleGameEngine.baseWeapon.minAtk)
      ,ConstantPropertyOverride(targetId, "WEAPON_MAX_ATK", BattleGameEngine.baseWeapon.maxAtk)
      ,ConstantPropertyOverride(targetId, "WEAPON_FATIGUE_TURNS", BattleGameEngine.baseWeapon.fatigueTurns)
      ,ConstantPropertyOverride(targetId, "WEAPON_DAMAGE_TYPE", BattleGameEngine.baseWeapon.damageType)
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
      val weaponDamageType = entityResolver.getEntityProperty(fromEntityId, "WEAPON_DAMAGE_TYPE")
      val damageResist = entityResolver.getEntityProperty(targetId, "DAMAGE_RESIST_" + weaponDamageType)
      // TODO: use a randomLongFromTo method
      val weaponAtkValue = RngUtils.randomIntFromTo(weaponMinAtk.toInt, weaponMaxAtk.toInt, ruleEngineParameters.rng).toLong
      val weaponAtkValueAfterResist = MathUtils.clampedMinus(weaponAtkValue, damageResist, 0)
      Seq(
        PlusPropertyOverride(entityResolver, targetId, "HP", - weaponAtkValueAfterResist)
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

class RaiseResist(resistType: Long, amt: Long) extends DefaultRule {
  override def label = "HEAL"

  override def createOverrides(fromEntityId: String, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[AbstractPropertyOverride] = {
    Seq(
      PlusPropertyOverride(entityResolver, fromEntityId, "DAMAGE_RESIST_" + resistType, amt)
    )
  }
}