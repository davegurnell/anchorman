// Publishing

inThisBuild(
  Seq(
    organization := "com.davegurnell",
    scalaVersion := "2.12.8",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/davegurnell/anchorman")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/davegurnell/anchorman.git"),
        "scm:git@github.com:davegurnell/anchorman.git"
      )
    ),
    developers := List(
      Developer(
        id = "davegurnell",
        name = "Dave Gurnell",
        email = "dave@cartographer.io",
        url = url("https://twitter.com/davegurnell")
      )
    ),
    pgpPublicRing := file("./travis/local.pubring.asc"),
    pgpSecretRing := file("./travis/local.secring.asc"),
    releaseEarlyEnableLocalReleases := true,
  )
)

// Command Aliases

addCommandAlias(
  "ci",
  ";clean ;coverage ;compile ;test ;coverageReport"
)

addCommandAlias("release", ";releaseEarly ;sonatypeReleaseAll")

// Common Project Settings

val commonScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-Xfatal-warnings",
  "-Ypartial-unification",
)

val commonLibraryDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % IntegrationTest
)

def commonScalafmtSettings =
  Seq(scalafmtOnCompile := true) ++
    inConfig(IntegrationTest)(
      org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings
    )

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
      "com.davegurnell"        %% "unindent"         % "1.1.0",
      "joda-time"              % "joda-time"         % "2.8.1",
      "org.apache.poi"         % "poi"               % "3.14",
      "org.apache.poi"         % "poi-ooxml"         % "3.14",
      "org.apache.poi"         % "poi-ooxml-schemas" % "3.14",
      "org.apache.xmlgraphics" % "fop"               % "2.1",
      "org.scala-lang.modules" %% "scala-xml"        % "1.2.0",
      "org.typelevel"          %% "cats-core"        % "1.4.0",
    ),
    // releaseEarlyPublish := PgpKeys.publishSigned.value
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
      "com.typesafe.play" %% "play-ws-standalone"     % "2.1.2",
      "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.1.2"
    ),
    // releaseEarlyPublish := PgpKeys.publishSigned.value
  )

lazy val anchorman = project
  .in(file("."))
  .aggregate(anchormanCore, anchormanPlay)
  .settings(
    packagedArtifacts := Map.empty,
    publishArtifact := false,
    publishLocal := {},
    publish := {},
    skip in publish := true
  )
