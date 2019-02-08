import ReleaseTransformations._


// publishing
publishMavenStyle in ThisBuild := true

credentials in ThisBuild += Credentials(Path.userHome / ".ivy2" / ".credentials_sonatype")

publishTo in ThisBuild := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository in ThisBuild := { _ => false }

pomExtra in ThisBuild := {
    <url>https://github.com/NoamShaish/containers</url>
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
releaseTagName in ThisBuild := s"${name.value}-${(version in ThisBuild).value}"

// sign artifacts

releasePublishArtifactsAction in ThisBuild := PgpKeys.publishSigned.value


// don't push changes (so they can be verified first)
releaseProcess in ThisBuild += releaseStepCommand("sonatypeRelease")
