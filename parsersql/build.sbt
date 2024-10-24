val scala3Version = "3.5.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "parsersql",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "com.lihaoyi" %% "fastparse" % "3.1.1"
    )
  )
