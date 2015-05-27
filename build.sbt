name := """TodoRest"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.webjars" %% "webjars-play" % "2.3.0-3",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "bootstrap" % "3.3.4" exclude("org.webjars", "jquery"),
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
