package be.rubenpieters.gre.battlegame

import be.rubenpieters.gre._
import be.rubenpieters.gre.utils.RngUtils
import be.rubenpieters.utils.MathUtils

import scala.util.Random

/**
  * Created by ruben on 11/09/16.
  */
object BattleApp extends App {
  import BattleGameGeneralRuleset._
  import BattleGameRuleSet1._
  import BattleGameRuleSet2._

  val ruleEngineParameters = RuleEngineParameters(new Random(1))

  val entity1 = Entity(
    "p1"
    ,entityBasicProperties ++ Map("INITIATIVE" -> 0L, "IN_INC" -> 1L, "IN_DEC" -> 1L)
    ,Map()
    ,Map()
    ,ruleEngineParameters
    ,ShuffledCyclicRuleWithRepresentationStrategy.fromRuleSeq(
      Seq(
        (equipRuleSet1Weapon, 2)
        //,(new RaiseShieldRule, 2)
        ,(new GenerateResource(1, 1), 2)
        ,(new AttackWithWeaponRule("p2"), 2)
      )
      ,ruleEngineParameters
    )
  )
    .applyRule(equipBaseWeapon, "p1")
    .asInstanceOf[Entity]

  val entity2 = Entity(
    "p2"
    ,entityBasicProperties ++ Map("INITIATIVE" -> 0L, "IN_INC" -> 1L, "IN_DEC" -> 1L)
    ,Map()
    ,Map()
    ,ruleEngineParameters
    ,ShuffledCyclicRuleWithRepresentationStrategy.fromRuleSeq(
      Seq(
        (equipRuleSet2Weapon, 2)
        ,(new GenerateResource(1, 1), 2)
        ,(new AttackWithWeaponRule("p1"), 2)
      )
      ,ruleEngineParameters
    )
  )
    .applyRule(equipBaseWeapon, "p2")
    .asInstanceOf[Entity]

  val scopeEntity = Entity(
    "scope"
    ,Map()
    ,Map("p1" -> entity1, "p2" -> entity2)
    ,Map()
    ,ruleEngineParameters
    ,null
  )

  val entityStream = Stream.iterate(scopeEntity){
    entity => entity.advance.asInstanceOf[Entity]
  }
  println("-- stream")
  entityStream.takeWhile { scope =>
    scope.getEntityProperty("p1" ,"HP") > 0 && scope.getEntityProperty("p2", "HP") > 0
  }.foreach{ x =>
    println(x.getEntityUnsafe("p1").properties, x.getEntityUnsafe("p2").properties)
    println(x.getEntityUnsafe("p1").appliedEffects, x.getEntityUnsafe("p2").appliedEffects)
    println(x.getEntityUnsafe("p1").ruleAdvanceStrategy, x.getEntityUnsafe("p2").ruleAdvanceStrategy)
    println((x.getEntityUnsafe("p1").getProperty("HP"), x.getEntityUnsafe("p1").getProperty("RESOURCE_1")), (x.getEntityUnsafe("p2").getProperty("HP"), x.getEntityUnsafe("p2").getProperty("RESOURCE_1")))
  }
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
  val equipBaseWeapon = new EquipWeaponRule(baseWeapon)

  class AttackWithWeaponRule(targetId: String) extends AbstractRule {
    override def label = "ATTACK_WPN"

    override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver,
                                  ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
      val currentResource1 = entityResolver.getEntityProperty(actingEntity, "RESOURCE_1")
      val weaponFatigueTurns = entityResolver.getEntityProperty(actingEntity, "WEAPON_FATIGUE_TURNS")
      if (currentResource1 >= weaponFatigueTurns) {
        // Fatigue Turns is zero -> able to attack
        val weaponMinAtk = entityResolver.getEntityProperty(actingEntity, "WEAPON_MIN_ATK")
        val weaponMaxAtk = entityResolver.getEntityProperty(actingEntity, "WEAPON_MAX_ATK")
        val weaponDamageType = entityResolver.getEntityProperty(actingEntity, "WEAPON_DAMAGE_TYPE")
        val damageResist = entityResolver.getEntityProperty(targetId, "DAMAGE_RESIST_" + weaponDamageType)
        // TODO: use a randomLongFromTo method
        val weaponAtkValue = RngUtils.randomIntFromTo(weaponMinAtk.toInt, weaponMaxAtk.toInt, ruleEngineParameters.rng).toLong
        val weaponAtkValueAfterResist = MathUtils.clampedMinus(weaponAtkValue, damageResist, 0)
        Seq(
          (targetId, PlusPropertyOverride(entityResolver, targetId, "HP", - weaponAtkValueAfterResist))
          ,(actingEntity, MinusPropertyOverride(entityResolver, actingEntity, "RESOURCE_1", weaponFatigueTurns))
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

object BattleGameRuleSet1 {
  import BattleGameGeneralRuleset._

  val ruleSet1Weapon = Weapon(5, 10, 3, 1)

  val equipRuleSet1Weapon = new EquipWeaponRule(ruleSet1Weapon)

  class RaiseShieldRule() extends AbstractRule {
    override def label = "RAISE_SHIELD"

    override def createOperations(actingEntity: EntityId, entityResolver: EntityResolver,
                                  ruleEngineParameters: RuleEngineParameters): Seq[(EntityId, Operation)] = {
      Seq(
        (actingEntity, SimpleAddEffectOperation(new RaiseShieldEffect(EffectEnding), actingEntity))

      ) ++ allDamageTypes.map{ dt => (actingEntity, PlusPropertyOverride(entityResolver, actingEntity, dt, 5))}
    }

    class RaiseShieldEffect(effectState: EffectState) extends Effect(effectState) {
      override def createWithNewState(effectState: EffectState): Effect = new RaiseShieldEffect(effectState)

      override def createOperations(actingEntity: EntityId, targetEntity: Entity, entityResolver: EntityResolver, ruleEngineParameters: RuleEngineParameters): Seq[Operation] = {
        effectState match {
          case EffectRunning(i) => Seq()
          case EffectEnding => allDamageTypes.map{ dt => MinusPropertyOverride(entityResolver, actingEntity, dt, 5)}
        }
      }
    }
  }
}

object BattleGameRuleSet2 {
  import BattleGameGeneralRuleset._

  val ruleSet2Weapon = Weapon(2, 4, 1, 2)

  val equipRuleSet2Weapon = new EquipWeaponRule(ruleSet2Weapon)
}