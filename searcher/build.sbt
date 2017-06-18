import sbt.Keys._
import scala.collection.JavaConverters._
import sbt.complete.Parsers.spaceDelimited

name := """searcher"""
organization := "com.lkuligin"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

fork in run := true
fork in Test := true

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.0" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.typesafe.play" %% "play-json" % "2.5.4",
  "com.google.inject" % "guice" % "4.0",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.jsuereth" %% "scala-arm" % "1.4",
  "com.typesafe.play" %% "play-ws" % "2.4.0-M2",
  "com.typesafe" % "config" % "1.3.0",
  "io.reactivex" % "rxscala_2.11" % "0.26.2"
) ++ testDependencies

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.lkuligin.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.lkuligin.binders._"
