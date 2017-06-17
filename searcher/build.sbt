name := """searcher"""
organization := "com.lkuligin"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.typesafe.play" %% "play-json" % "2.5.4",
  "com.google.inject" % "guice" % "4.0",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.jsuereth" %% "scala-arm" % "1.4"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.lkuligin.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.lkuligin.binders._"
