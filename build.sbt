organization        in ThisBuild := "com.davegurnell"
version             in ThisBuild := "0.4.0-SNAPSHOT"

scalaVersion        in ThisBuild := "2.12.4"
crossScalaVersions  in ThisBuild := Seq("2.11.10", "2.12.4")

val commonScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-Ypartial-unification"
)

val commonLibraryDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % IntegrationTest
)

val sonatypeSettings = Seq(
  publishTo := sonatypePublishTo.value,
  sonatypeProfileName := "com.davegurnell",
  publishMavenStyle := false,
  licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
  homepage := Some(url("https://github.com/davegurnell/anchorman")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/davegurnell/anchorman"),
    "scm:git@github.com:davegurnell/anchorman.git"
  )),
  developers := List(
    Developer(
      id="davegurnell",
      name="Dave Gurnell",
      email="dave@underscore.io",
      url=url("https://davegurnell.com")
    )
  )
)

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val core = project.in(file("core"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(sonatypeSettings)
  .settings(
    name := "anchorman-core",
    libraryDependencies ++= Seq(
      "com.davegurnell"        %% "unindent"          % "1.1.0",
      "com.typesafe.play"      %% "play-ws"           % "2.6.10",
      "joda-time"               % "joda-time"         % "2.8.1",
      "org.apache.poi"          % "poi"               % "3.14",
      "org.apache.poi"          % "poi-ooxml"         % "3.14",
      "org.apache.poi"          % "poi-ooxml-schemas" % "3.14",
      "org.apache.xmlgraphics"  % "fop"               % "2.1",
      "org.scala-lang.modules" %% "scala-xml"         % "1.0.6",
      "org.typelevel"          %% "cats-core"         % "1.0.1"
    ),
    libraryDependencies ++= commonLibraryDependencies,
    scalacOptions ++= commonScalacOptions
  )

lazy val play = project.in(file("play"))
  .dependsOn(core)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(sonatypeSettings)
  .settings(
    name := "anchorman-play",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-ws"     % "2.6.0-M1",
      "com.typesafe.akka" %% "akka-stream" % "2.4.17"   % IntegrationTest,
      "com.typesafe.play" %% "play-ahc-ws" % "2.6.0-M1" % IntegrationTest
    ),
    libraryDependencies ++= commonLibraryDependencies
  )

lazy val root = project.in(file("."))
  .aggregate(core, play)
  .settings(
    publishArtifact := false,
    publishLocal := {},
    publish := {}
  )