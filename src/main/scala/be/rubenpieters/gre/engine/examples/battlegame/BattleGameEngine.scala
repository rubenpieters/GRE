package be.rubenpieters.gre.engine.examples.battlegame

import be.rubenpieters.gre.rules.{AbstractRule, SetPropertyRule}
import be.rubenpieters.gre.rules.meta.ComposedRule

/**
  * Created by rpieters on 7/08/2016.
  */
object BattleGameEngine {

  def equipWeaponRule(weapon: Weapon): AbstractRule = {
    new ComposedRule(
      "EQUIP_WEAPON_RULE",
      Seq(
        new SetPropertyRule("EWR_MIN_ATK", "", "WEAPON_MIN_ATK", weapon.minAtk),
        new SetPropertyRule("EWR_MAX_ATK", "", "WEAPON_MIN_ATK", weapon.maxAtk),
        new SetPropertyRule("EWR_FAT_TURNS", "", "WEAPON_FATIGUE_TURNS", weapon.fatigueTurns)
      )
    )
  }

}

case class Weapon(minAtk: Int, maxAtk: Int, fatigueTurns: Int)
