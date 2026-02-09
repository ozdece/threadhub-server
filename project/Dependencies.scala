
object Dependencies {
  import sbt.*

  private val xml = Seq("org.scala-lang.modules" %% "scala-xml" % "2.4.0")

  private val typelevel = Seq(
    "org.typelevel" %% "cats-core" % "2.13.0"
  )

  private val enumeratum = Seq(
    "com.beachape" %% "enumeratum" % "1.9.4"
  )

  private val testing = Seq(
    "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )

  private val zio = Seq(
    "dev.zio" %% "zio" % "2.1.24",
    "dev.zio" %% "zio-http" % "3.8.1",
    "dev.zio" %% "zio-streams" % "2.1.24"
  )

  val all = xml ++ typelevel ++ enumeratum ++ testing ++ zio
}
