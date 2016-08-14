package be.rubenpieters.gre.engine.examples.battlegame

import be.rubenpieters.gre.entity.ImmutableEntityManager
import be.rubenpieters.gre.rules.{DefaultRule, PropertyOverride}

/**
  * Created by rpieters on 7/08/2016.
  */
object BattleGameEngine {


}

case class Weapon(minAtk: Int, maxAtk: Int, fatigueTurns: Int)


class EquipWeaponRule(weapon: Weapon) extends DefaultRule {
  override def label = "EQUIP_WPN"

  override def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride] = {
    Seq(
      PropertyOverride(fromEntityId, "WEAPON_MIN_ATK", weapon.minAtk)
      ,PropertyOverride(fromEntityId, "WEAPON_MAX_ATK", weapon.maxAtk)
      ,PropertyOverride(fromEntityId, "WEAPON_FATIGUE_TURNS", weapon.fatigueTurns)
    )
  }
}

object AttackWithWeaponRule extends DefaultRule {
  override def label = "ATTACK_WPN"

  override def createOverrides(fromEntityId: String, immutableEntityManager: ImmutableEntityManager): Seq[PropertyOverride] = {

    Seq(

    )
  }
}