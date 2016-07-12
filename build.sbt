lazy val commonSettings = Seq(
  organization := "be.rubenpieters",
  version := "0.0.1",
  scalaVersion := "2.11.8"
)

name := "gre"

lazy val root = (project in file(".")).
// ScalaJS
// https://www.scala-js.org/tutorial/basic/
enablePlugins(ScalaJSPlugin)
// Node.js is a much more performant JavaScript engine.
// Disabling Rhino must be done once per sbt session. Alternatively, you can include the setting directly in your build.sbt,
// or, in order not to disturb your teammates, in a separate .sbt file (say, local.sbt):
scalaJSUseRhino in Global := false


libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.0"

)

// force scala version
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }