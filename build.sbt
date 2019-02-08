lazy val scala211 = "2.11.8"
lazy val sparkVersion = "2.3.1"

organization in ThisBuild := "com.github.NoamShaish"
version in ThisBuild:= "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := scala211
  publishTo in ThisBuild := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
lazy val root = (project in file("."))
  .aggregate(core, spark, example)
  .settings(
    publishArtifact := false
  )

lazy val core = (project in file("core"))
  .settings(
    name := "containers-core",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val spark = (project in file("spark"))
  .dependsOn(core)
  .settings(
    name := "containers-spark",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val example = (project in file("example"))
  .dependsOn(core, spark)
  .settings(
    name := "containers-example",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.scalanlp" %% "breeze" % "0.13.2",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

