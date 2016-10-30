lazy val commonSettings = Seq(
  organization := "be.rubenpieters",
  version := "0.0.1",
  scalaVersion := "2.11.8"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.8.0"
)