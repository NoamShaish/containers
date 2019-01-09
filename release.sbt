import ReleaseTransformations._


// publishing
ThisBuild / publishMavenStyle := true

ThisBuild / credentials += Credentials(Path.userHome / ".ivy2" / ".credentials_sonatype")

ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

ThisBuild / publishArtifact in Test := false

ThisBuild / pomIncludeRepository := { _ => false }


licenses := Seq("he Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/NoamShaish/containers"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/NoamShaish/containers"),
    "scm:git@github.com:NoamShaish/containers.git"
  ))

developers := List(
  Developer(
    id="NoamShaish",
    name="Noam Shaish",
    email="noamshaish@gmail.com",
    url = url("https://github.com/NoamShaish")
  ))


// use maven style tag name
ThisBuild / releaseTagName := s"${name.value}-${(version in ThisBuild).value}"

// sign artifacts

ThisBuild / releasePublishArtifactsAction := PgpKeys.publishSigned.value

// don't push changes (so they can be verified first)
ThisBuild / releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges,
  releaseStepCommand("sonatypeRelease")
)