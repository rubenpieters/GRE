package main.scala.be.rubenpieters.gre.engine.examples.zombie

import be.rubenpieters.gre.entity.Entity
import be.rubenpieters.gre.rules.{AbstractRule, SinglePropertyOperationRule}

/**
  * Created by ruben on 13/07/2016.
  */
class SimpleZombieEngine {

  def gatherRawFoodAction() = {
    new SinglePropertyOperationRule(
      "village",
      (hp, player, ruleEngineParameters) =>
      {
        val rng = ruleEngineParameters.rng
        val currentRawFood = player.properties.get("RAW_FOOD").get
        val currentVillagers = player.properties.get("RAW_FOOD").get
        val gatheredFood = ((rng.nextInt(300) + 50) * currentVillagers) / 100
        val newTotal = currentRawFood + gatheredFood
        (newTotal,
          "villagers gather " + gatheredFood + " food, new raw food total: " + newTotal
          )
      },
      "village",
      "RAW_FOOD"
    )
  }
  def gatherRawMaterialsAction() = {
    new SinglePropertyOperationRule(
      "village",
      (hp, player, ruleEngineParameters) =>
      {
        val rng = ruleEngineParameters.rng
        val currentRawMaterials = player.properties.get("RAW_MATERIALS").get
        val currentVillagers = player.properties.get("RAW_MATERIALS").get
        val rawMaterials = ((rng.nextInt(50) + 50) * currentVillagers) / 100
        val newTotal = currentRawMaterials + rawMaterials
        (newTotal,
          "villagers gather " + rawMaterials + " food, new raw food total: " + newTotal
          )
      },
      "village",
      "RAW_FOOD"
    )
  }


  def zombiesEntity(uniqueId: String): Entity = {
    new Entity("zombies", uniqueId,
      Map("ZOMBIES" -> 0),
      Seq()
    )
  }

  def villageEntity(uniqueId: String, strategy: Seq[AbstractRule]): Entity = {
    new Entity("village", uniqueId,
      Map("VILLAGERS" -> 0, "FOOD" -> 0, "WEAPONS" -> 0, "BABIES" -> 0, "RAW_FOOD" -> 0, "RAW_MATERIALS" -> 0),
      strategy
    )
  }
}
