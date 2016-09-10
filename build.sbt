organization := "be.rubenpieters"
version := "0.0.1"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.7.2"

  ,"com.google.guava" % "guava" % "19.0"

  ,"com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
  ,"ch.qos.logback" %  "logback-classic" % "1.1.7"


  ,"org.scalatest" %% "scalatest" % "3.0.0" % "test"
  ,"org.mockito" % "mockito-all" % "1.9.5" % "test"
)