organization := "be.rubenpieters"
version := "0.0.1"
scalaVersion := "2.11.8"

lazy val root =
  project.in(file("."))
    .configs( IntegrationTest )
    .settings( Defaults.itSettings : _*)
    .settings(
      testOptions in Test := Seq(Tests.Filter(itFilter)),
      testOptions in IntegrationTest := Seq(Tests.Filter(unitFilter))
    )

def itFilter(name: String): Boolean = name endsWith "IT"
def unitFilter(name: String): Boolean = (name endsWith "Test") && !itFilter(name)

val doobieVersion = "0.3.1-SNAPSHOT"
val catsVersion = "0.7.2"
val fs2Version = "0.9.0"
val logbackVersion = "1.1.7"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % catsVersion
  , "org.tpolecat" %% "doobie-core-cats" % doobieVersion
  , "org.tpolecat" %% "doobie-postgres-cats" % doobieVersion
  , "co.fs2" %% "fs2-core" % fs2Version
  , "com.github.mpilquist" %% "simulacrum" % "0.10.0"

  // LOGGING
  ,"ch.qos.logback" % "logback-core" % logbackVersion
  ,"ch.qos.logback" % "logback-classic" % logbackVersion

  // TEST DEPENDENCIES
  , "org.scalatest" %% "scalatest" % "2.2.6" % "it,test"
  , "org.scalacheck" %% "scalacheck" % "1.12.5" % "it,test"

  , "org.testcontainers" % "testcontainers" % "1.1.5" % "it,test"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

scalacOptions in Test ++= Seq("-Yrangepos")