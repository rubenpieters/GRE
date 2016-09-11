package be.rubenpieters.gre.battlegame

import be.rubenpieters.gre._
import be.rubenpieters.gre.utils.RngUtils
import be.rubenpieters.utils.MathUtils

/**
  * Created by ruben on 11/09/16.
  */
object BattleApp extends App {

}

object BattleGameGeneralRuleset {

  case class Weapon(minAtk: Int, maxAtk: Int, fatigueTurns: Int, damageType: Int)


  val entityBasicProperties: Map[String, Long] =
    Map(
      "HP" -> 100
      ,"MAXHP" -> 100
      ,"DAMAGE_RESIST_1" -> 0
      ,"DAMAGE_RESIST_2" -> 0
      ,"DAMAGE_RESIST_3" -> 0
      ,"DAMAGE_RESIST_4" -> 0
      ,"DAMAGE_RESIST_5" -> 0
      ,"RESOURCE_1" -> 0
      ,"RESOURCE_2" -> 0
      ,"RESOURCE_3" -> 0
      ,"RESOURCE_4" -> 0
      ,"RESOURCE_5" -> 0
    )

  val allDamageTypes = (1 to 5 ).map { x => s"DAMAGE_RESIST_$x"}

  val baseWeapon = Weapon(1, 1, 0, 1)

  class AttackWithWeaponRule(targetId: String) extends AbstractRule {
    override def label = "ATTACK_WPN"

    override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver,
                                  ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
      val currentFatigueTurns = entityResolver.getEntityProperty(actingEntity, "FATIGUE_TURNS")
      if (currentFatigueTurns == 0) {
        // Fatigue Turns is zero -> able to attack
        val weaponMinAtk = entityResolver.getEntityProperty(actingEntity, "WEAPON_MIN_ATK")
        val weaponMaxAtk = entityResolver.getEntityProperty(actingEntity, "WEAPON_MAX_ATK")
        val weaponFatigueTurns = entityResolver.getEntityProperty(actingEntity, "WEAPON_FATIGUE_TURNS")
        val weaponDamageType = entityResolver.getEntityProperty(actingEntity, "WEAPON_DAMAGE_TYPE")
        val damageResist = entityResolver.getEntityProperty(targetId, "DAMAGE_RESIST_" + weaponDamageType)
        // TODO: use a randomLongFromTo method
        val weaponAtkValue = RngUtils.randomIntFromTo(weaponMinAtk.toInt, weaponMaxAtk.toInt, ruleEngineParameters.rng).toLong
        val weaponAtkValueAfterResist = MathUtils.clampedMinus(weaponAtkValue, damageResist, 0)
        Seq(
          (targetId, PlusPropertyOverride(entityResolver, targetId, "HP", - weaponAtkValueAfterResist))
          ,(actingEntity, ConstantPropertyOverride(actingEntity, "FATIGUE_TURNS", weaponFatigueTurns))
        )
      } else {
        // Fatigue Turns is not zero -> fumble attack, nothing happens
        Seq()
      }
    }
  }

  class EquipWeaponRule(weapon: Weapon) extends AbstractRule {
    override def label = "EQUIP_WPN"

    override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver,
                                  ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
      Seq(
        (actingEntity, ConstantPropertyOverride(actingEntity, "WEAPON_MIN_ATK", weapon.minAtk))
        ,(actingEntity, ConstantPropertyOverride(actingEntity, "WEAPON_MAX_ATK", weapon.maxAtk))
        ,(actingEntity, ConstantPropertyOverride(actingEntity, "WEAPON_FATIGUE_TURNS", weapon.fatigueTurns))
        ,(actingEntity, ConstantPropertyOverride(actingEntity, "WEAPON_DAMAGE_TYPE", weapon.damageType))
      )
    }
  }

  class GenerateResource(resourceType: Long, amt: Long) extends AbstractRule {
    override def label = "GEN_RES_" + resourceType

    override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver,
                                  ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
      Seq(
        (actingEntity, PlusPropertyOverride(entityResolver, actingEntity, "RESOURCE_" + resourceType, amt))
      )
    }
  }

}
