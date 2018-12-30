lazy val scala211 = "2.11.8"
lazy val supportedScalaVersions = List(scala211)
lazy val sparkVersion = "2.3.1"

ThisBuild / organization := "org.noam.shaish"
ThisBuild / version      := "0.1.0-SNAPSHOT"


lazy val root = (project in file("."))
  .settings(
    name := "containers",
      // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.scalanlp" %% "breeze" % "0.13.2",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

