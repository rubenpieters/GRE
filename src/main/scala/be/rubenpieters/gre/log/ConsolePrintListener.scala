package be.rubenpieters.gre.log

/**
  * Created by rpieters on 14/05/2016.
  */
object ConsolePrintListener extends LogListener {
  override def log(logLine: String): Unit = {
    println(logLine)
  }
}
