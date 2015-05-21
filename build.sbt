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
  "org.webjars" % "angularjs" % "1.3.15" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap" % "3.3.4" exclude("org.webjars", "jquery"),
  "joda-time" % "joda-time" % "2.3",
  "jp.t2v" %% "play2-auth"      % "0.13.2",
  "jp.t2v" %% "play2-auth-test" % "0.13.2" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
