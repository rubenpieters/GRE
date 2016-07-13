package main.scala.be.rubenpieters.gre.engine.examples.zombie

import java.util.UUID

import be.rubenpieters.gre.endcondition.EndCondition
import be.rubenpieters.gre.engine.{EngineFactory, EngineRunner}
import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.log.ConsolePrintListener
import be.rubenpieters.gre.rules.{AbstractRule, RuleEngineParameters, SinglePropertyOperationRule}

/**
  * Created by ruben on 13/07/2016.
  */
object SimpleZombieEngine {

  def zombiesEntity(uniqueId: String): Entity = {
    new Entity("zombies", uniqueId,
      Map("ZOMBIES" -> 1000),
      Seq(new ZombieTurnRule())
    )
  }

  def villageEntity(uniqueId: String): Entity = {
    new Entity("village", uniqueId,
      Map("VILLAGERS" -> 100, "FOOD" -> 500, "WEAPONS" -> 100),
      Seq(new VillagerTurnRule())
    )
  }

  def createNewEntities(): Set[Entity] = {
    val dealer = zombiesEntity("zombies")
    val player = villageEntity("village")
    Set(dealer, player)
  }

  def factory(log: Boolean): EngineFactory = {
    new EngineFactory(
      Seq("village", "zombies"),
      createNewEntities,
      if (log) {Set(ConsolePrintListener)} else {Set()},
      Set(NoVillagersEndCondition, NoZombiesEndCondition)
    )
  }

}


object NoZombiesEndCondition extends EndCondition {
  override def checkCondition(engineRunner: EngineRunner): Option[String] = {
    if (engineRunner.entityManager.getEntity("zombies").properties.get("ZOMBIES").get <= 0) {
      Some("Zombies Lose")
    } else {
      None
    }
  }
}

object NoVillagersEndCondition extends EndCondition {
  override def checkCondition(engineRunner: EngineRunner): Option[String] = {
    if (engineRunner.entityManager.getEntity("village").properties.get("VILLAGERS").get <= 0) {
      Some("Villagers Lose")
    } else {
      None
    }
  }
}

class VillagerTurnRule(
                        label: String = UUID.randomUUID().toString
                      ) extends AbstractRule(label) {
  val recruitModifier = 10
  val gatherModifier = 200
  val smithModifier = 20

  override def cost: Long = 1

  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    val recruitPercentage = 33
    val gatherPercentage = 33
    val smithPercentage = 33
    val villagerCount = fromEntity.properties.get("VILLAGERS").get
    val foodCount = fromEntity.properties.get("FOOD").get
    val weaponCount = fromEntity.properties.get("WEAPONS").get
    val newVillagers = villagerCount * recruitPercentage * recruitModifier / 10000
    val newFood = villagerCount * gatherPercentage * gatherModifier / 10000
    val newWeapons = villagerCount * smithPercentage * smithModifier / 10000

    val newFoodValueCalc = foodCount + newFood - villagerCount
    val (newFoodValue, starvingVillagers) = if (newFoodValueCalc < 0) {
      (0L, math.abs(newFoodValueCalc))
    } else {
      (newFoodValueCalc, 0L)
    }
    fromEntity.properties = fromEntity.properties +
      ("VILLAGERS" -> (villagerCount + newVillagers - starvingVillagers),
        "FOOD" -> newFoodValue
        ,"WEAPONS" -> (weaponCount + newWeapons)
        )
    "Village Turn: V" + fromEntity.properties
  }
}

class ZombieTurnRule(
                      label: String = UUID.randomUUID().toString
                    ) extends AbstractRule(label) {
  val zombieAttackModifier = 5

  override def cost: Long = 1

  override def apply(fromEntity: Entity, ruleEngineParameters: RuleEngineParameters): String = {
    val rng = ruleEngineParameters.rng
    val village = ruleEngineParameters.entityManager.getEntity("village")

    val attackPercentage = rng.nextInt(100)
    val zombiesCount = fromEntity.properties.get("ZOMBIES").get
    val zombieAttackValue = zombiesCount * attackPercentage * zombieAttackModifier / 10000

    val villagerCount = village.properties.get("VILLAGERS").get
    val foodCount = village.properties.get("FOOD").get
    val weaponCount = village.properties.get("WEAPONS").get

    val villageAttackValue = math.min(villagerCount, weaponCount)

    val newZombiesCount = zombiesCount - villageAttackValue
    val newVillagerCount = villagerCount - zombieAttackValue

    village.properties = village.properties +
      ("VILLAGERS" -> newVillagerCount)
    fromEntity.properties = fromEntity.properties +
      ("ZOMBIES" -> newZombiesCount)
    "Zombie Attack: Z" + fromEntity.properties + " V" + village.properties
  }
}
