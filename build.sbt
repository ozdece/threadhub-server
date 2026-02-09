ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "threadhub-server",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalaVersion := "2.13.18",
    scalacOptions := List("-Yrangepos", "-Wunused:imports")
  )

libraryDependencies ++= List(
  "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
  "org.typelevel" %% "cats-core" % "2.13.0",
  "com.beachape" %% "enumeratum" % "1.9.4",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

addCommandAlias("lint", "scalafixAll;scalafmtAll;")
