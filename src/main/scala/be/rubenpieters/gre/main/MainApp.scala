package be.rubenpieters.gre.main

import org.scalajs.dom
import org.scalajs.dom.document

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

/**
  * Created by rpieters on 14/05/2016.
  */
object MainApp extends JSApp {
  def main(): Unit = {
    appendPar(document.body, "Running GRE MainApp...")
  }

  @JSExport
  def addClickedMessage(): Unit = {
    appendPar(document.body, "You clicked the button!")
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }
}
