import sbt.Keys.{credentials, publishArtifact, publishTo}
import sbt.{Credentials, url}

lazy val scala211 = "2.11.8"
lazy val sparkVersion = "2.3.1"
lazy val repoUrl = "https://github.com/NoamShaish/containers"

organization in ThisBuild := "com.github.NoamShaish"
version in ThisBuild := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := scala211

val appSettings = Seq(
  publishMavenStyle := true,
  credentials := Seq(
    Credentials(Path.userHome / ".ivy2" / ".credentials_sonatype")),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  homepage := Some(url(repoUrl)),
  scmInfo := homepage.value.map(
    url =>
      ScmInfo(
        url,
        "scm:git:git@github.com:NoamShaish/containers.git",
        "scm:git:git@github.com:NoamShaish/containers.git"
    )),
  developers := List(
    Developer(
      id = "NoamShaish",
      name = "Noam Shaish",
      email = "noamshaish@gmail.com",
      url = url(repoUrl)
    )
  ),
  licenses := List(
    (
      "The Apache Software License, Version 2.0",
      url("http://www.apache.org/licenses/LICENSE-2.0.txt")
    )
  ),
// use maven style tag name
  releaseTagName := s"${name.value}-${(version in ThisBuild).value}",
// sign artifacts
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess += releaseStepCommand("sonatypeRelease")
)

lazy val root = (project in file("."))
  .aggregate(core, spark, example)
  .settings(appSettings: _*)
  .settings(
    publishArtifact := false
  )

lazy val core = (project in file("core"))
  .settings(appSettings: _*)
  .settings(
    name := "containers-core",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val spark = (project in file("spark"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(appSettings: _*)
  .settings(
    name := "containers-spark",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "com.holdenkarau" %% "spark-testing-base" % s"${sparkVersion}_0.10.0" % "test"
    )
  )

lazy val example = (project in file("example"))
  .dependsOn(core, spark)
  .settings(appSettings: _*)
  .settings(
    name := "containers-example",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.scalanlp" %% "breeze" % "0.13.2",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )
