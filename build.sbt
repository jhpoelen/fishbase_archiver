import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "com.github.jhpoelen",
  version := "0.1.1",
  scalaVersion := "2.11.8"
)

lazy val archiveFishbase = taskKey[Unit]("Archive fishbase tables")

archiveFishbase := {
  Seq("sh", "archive_fishbase.sh") !
}


lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "fishbase_archiver",
    resolvers += Resolver.sonatypeRepo("public"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.5" % "test"
    ),
    test in assembly := {}
  )

