import ReleaseTransformations._


// publishing
publishMavenStyle := true

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials_sonatype")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := {
    <licenses>
      <license>
        <name>The Apache Software License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:git@github.com:NoamShaish/containers.git</connection>
      <developerConnection>scm:git:git@github.com:NoamShaish/containers.git</developerConnection>
      <url>https://github.com/NoamShaish/containers</url>
    </scm>
    <developers>
      <developer>
        <id>NoamShaish</id>
        <name>Noam Shaish</name>
        <email>noamshaish@gmail.com</email>
      </developer>
    </developers>
}

// use maven style tag name
releaseTagName := s"${name.value}-${(version in ThisBuild).value}"

// sign artifacts

releasePublishArtifactsAction := PgpKeys.publishSigned.value

// don't push changes (so they can be verified first)
releaseProcess := Seq(
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