package be.rubenpieters.gre.main

import be.rubenpieters.gre.engine.examples.simplegame.SimpleGameEngine
import be.rubenpieters.gre.log.LogListener
import org.scalajs.dom
import org.scalajs.dom.document

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

/**
  * Created by rpieters on 14/05/2016.
  */
object MainApp extends JSApp {
  val engineRunner = SimpleGameEngine.simpleEngineWithLoggers(Set(AppendParLogLister))

  def main(): Unit = {
    appendPar(document.body, "GRE")
  }

  @JSExport
  def addClickedMessage(): Unit = {
    engineRunner.runStep()
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }
}

object AppendParLogLister extends LogListener {
  override def log(logLine: String): Unit = {
    MainApp.appendPar(document.body, logLine)
  }
}
