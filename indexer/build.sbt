import sbt.Keys._

import scala.collection.JavaConverters._
import sbt.complete.Parsers.spaceDelimited

scalaVersion := "2.11.8"
name := "indexer"
organization := "com.lkuligin"
version := "1.0"

fork in run := true
fork in Test := true

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  //.enablePlugins(PlayScala)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "info"
  )


libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.0" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.typesafe.play" %% "play-json" % "2.6.0-RC2",
  //"net.codingwell" %% "scala-guice" % "4.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.jsuereth" %% "scala-arm" % "2.0",
  "com.typesafe" % "config" % "1.3.0",
  "io.reactivex" % "rxscala_2.11" % "0.26.2",
  "com.typesafe.play" %% "play-ws" % "2.4.0-M2"
) ++ testDependencies

mainClass in (Compile, run) := Some("com.lkuligin.indexer.Application")

val configResource = System.getProperty("config.resource") match {
  case null => "application.conf"
  case value => value
}


fork in Test := true
fork in run := true

resourceDirectory in Test := baseDirectory.value / "src/test/resources"

javaOptions in run ++= Seq(
  s"-Dconfig.resource=$configResource",
  "-Dlogback.configurationFile=logback.xml"
)