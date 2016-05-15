package be.rubenpieters.gre.endcondition
import be.rubenpieters.gre.engine.EngineRunner

/**
  * Created by rpieters on 14/05/2016.
  */
object ZeroHpEndCondition extends EndCondition {
  override def checkCondition(engineRunner: EngineRunner): Option[String] = {
    val checkResult = engineRunner.entityManager.entityMap.exists { case (id, entity) =>
      entity.properties.get("HP") match {
        case Some(hp) if hp <= 0 => true
        case _ => false
      }
    }
    if (checkResult) {
      Some("Engine ended because an entity reached 0 HP")
    } else {
      None
    }
  }
}
