lazy val commonSettings = Seq(
  organization := "be.rubenpieters",
  version := "0.0.1",
  scalaVersion := "2.11.8"
)

val doobieVersion = "0.3.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.8.0"
  , "org.tpolecat" %% "doobie-core" % doobieVersion
  , "org.tpolecat" %% "doobie-contrib-postgresql" % doobieVersion
  , "org.tpolecat" %% "doobie-contrib-specs2" % doobieVersion
)