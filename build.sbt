ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "threadhub-server",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalaVersion := "2.13.18",
    scalacOptions := List("-Yrangepos", "-Wunused:imports")
  )

libraryDependencies ++= Dependencies.all

addCommandAlias("lint", "scalafixAll;scalafmtAll;")
