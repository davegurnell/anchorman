organization in ThisBuild := "com.davegurnell"

scalaVersion in ThisBuild := "2.13.1"
crossScalaVersions in ThisBuild := Seq("2.12.12", "2.13.1")

// Common Project Settings

val commonScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-Xfatal-warnings",
)

val commonLibraryDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % IntegrationTest
)

def commonScalafmtSettings =
  Seq(scalafmtOnCompile := true) ++
    inConfig(IntegrationTest)(
      org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings
    )

resolvers in ThisBuild ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
)

// Versioning

// A lot of the versioning, publishing, and Travis-related code below is adapted from:
//
//   - https://alexn.org/blog/2017/08/16/automatic-releases-sbt-travis.html
//   - http://caryrobbins.com/dev/sbt-publishing/

enablePlugins(GitVersioning)
enablePlugins(GitBranchPrompt)

// Use "1.2.3-4-aabbccdde-SNAPSHOT" versnining:
git.useGitDescribe := true

// Put "-SNAPSHOT" on a commit if it's not a tag:
git.gitUncommittedChanges := git.gitCurrentTags.value.isEmpty

// Publishing

publishMavenStyle := true

isSnapshot := version.value.endsWith("SNAPSHOT")

publishTo in ThisBuild := sonatypePublishTo.value

usePgpKeyHex("DF704C4F70202105")

pgpPublicRing := file("./travis/local.pubring.asc")

pgpSecretRing := file("./travis/local.secring.asc")

licenses in ThisBuild += ("Apache-2.0", url(
  "http://apache.org/licenses/LICENSE-2.0"
))

homepage in ThisBuild := Some(url("https://github.com/davegurnell/anchorman"))

scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/davegurnell/anchorman.git"),
    "scm:git@github.com:davegurnell/anchorman.git",
  )
)

developers in ThisBuild := List(
  Developer(
    id = "davegurnell",
    name = "Dave Gurnell",
    email = "dave@cartographer.io",
    url = url("https://twitter.com/davegurnell"),
  )
)

// Travis

// Sonatype credentials are on Travis in a secret:
credentials ++= {
  val travisCredentials = for {
    user <- sys.env.get("SONATYPE_USER")
    pass <- sys.env.get("SONATYPE_PASS")
  } yield Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    user,
    pass
  )

  travisCredentials.toSeq
}

// Password to the PGP certificate is on Travis in a secret:
pgpPassphrase := sys.env.get("PGP_PASS").map(_.toArray)

// Projects

lazy val anchormanCore = project
  .in(file("core"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonScalafmtSettings)
  .settings(
    name := "anchorman-core",
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= commonLibraryDependencies,
    libraryDependencies ++= Seq(
      "com.davegurnell" %% "unindent" % "1.3.1",
      "com.outr"        %% "hasher"   % "1.2.2",
      "joda-time"       % "joda-time" % "2.10.5",
      // "org.apache.poi"         % "poi"               % "4.1.1",
      // "org.apache.poi"         % "poi-ooxml"         % "4.1.1",
      // "org.apache.poi"         % "poi-ooxml-schemas" % "4.1.1",
      "org.apache.xmlgraphics" % "fop"        % "2.1",
      "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
      "org.typelevel"          %% "cats-core" % "2.1.0",
    )
  )

lazy val anchormanPlay = project
  .in(file("play"))
  .dependsOn(anchormanCore)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonScalafmtSettings)
  .settings(
    name := "anchorman-play",
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= commonLibraryDependencies,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-ws"     % "2.7.4" % Provided,
      "com.typesafe.play" %% "play-ahc-ws" % "2.7.4" % Provided
    )
  )

lazy val anchorman = project
  .in(file("."))
  .aggregate(anchormanCore, anchormanPlay)
  .settings(publishArtifact := false)

// Command Aliases

addCommandAlias("ci", ";clean ;coverage ;compile ;test ;coverageReport")
addCommandAlias("release", ";+publishSigned ;sonatypeReleaseAll")

// Formatting

scalafmtOnCompile := true
