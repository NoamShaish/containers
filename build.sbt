lazy val scala211 = "2.11.8"
lazy val supportedScalaVersions = List(scala211)
lazy val sparkVersion = "2.3.1"

ThisBuild / organization := "com.github.NoamShaish"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .aggregate(core, spark, example)
  .settings(
    crossScalaVersions := Nil
  )
lazy val core = (project in file("core"))
  .settings(
    name := "containers-core",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val spark = (project in file("spark"))
  .dependsOn(core)
  .settings(
    name := "containers-spark",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val example = (project in file("example"))
  .dependsOn(core, spark)
  .settings(
    name := "containers-example",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.scalanlp" %% "breeze" % "0.13.2",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

