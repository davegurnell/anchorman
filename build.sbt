enablePlugins(GitVersioning)
enablePlugins(GitBranchPrompt)

// Basic settings -------------------------------

ThisBuild / organization := "com.davegurnell"

ThisBuild / scalaVersion := "2.13.14"

ThisBuild / crossScalaVersions := Seq("2.13.14")

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
)

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("releases") ++ Resolver.sonatypeOssRepos("snapshots")

// Versioning -----------------------------------

ThisBuild / versionScheme := Some("early-semver")

git.gitUncommittedChanges := git.gitCurrentTags.value.isEmpty // Put "-SNAPSHOT" on a commit if it's not a tag

// Github Actions -------------------------------

ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17")
)

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")

ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

// Publishing -----------------------------------

publishMavenStyle := true

isSnapshot := version.value.endsWith("SNAPSHOT")

usePgpKeyHex("93EB089E23C95A4AAC03B32DE679A8D04452EE29")

ThisBuild / licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0"))

ThisBuild / homepage := Some(url("https://github.com/davegurnell/anchorman"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/davegurnell/anchorman.git"),
    "scm:git@github.com:davegurnell/anchorman.git",
  )
)

ThisBuild / developers := List(
  Developer(
    id = "davegurnell",
    name = "Dave Gurnell",
    email = "dave@cartographer.io",
    url = url("https://twitter.com/davegurnell"),
  )
)

// Projects

lazy val core = project
  .in(file("core"))
  .settings(
    name := "anchorman-core",
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= commonLibraryDependencies,
    libraryDependencies ++= Seq(
      "com.davegurnell"        %% "unindent"  % "1.3.1",
      "com.outr"               %% "hasher"    % "1.2.2",
      "joda-time"              % "joda-time"  % "2.10.5",
      "org.apache.xmlgraphics" % "fop"        % "2.1",
      "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
      "org.typelevel"          %% "cats-core" % "2.1.0",
    )
  )

lazy val coreIt = project
  .in(file("core-it"))
  .dependsOn(core % "compile;test;test->test")
  .settings(
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    publish / skip := true,
  )

lazy val play = project
  .in(file("play"))
  .dependsOn(core)
  .settings(
    name := "anchorman-play",
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= commonLibraryDependencies,
    libraryDependencies ++= Seq(
      "org.playframework" %% "play-ws"     % "3.0.0" % Provided,
      "org.playframework" %% "play-ahc-ws" % "3.0.0" % Provided,
      "javax.xml.bind"    % "jaxb-api"     % "2.3.1",
    )
  )

lazy val playIt = project
  .in(file("play-it"))
  .dependsOn(play % "compile;test;test->test")
  .settings(
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor" % "1.0.2",
      "org.playframework" %% "play-ws"     % "3.0.1",
      "org.playframework" %% "play-ahc-ws" % "3.0.1",
    )
  )

lazy val anchorman = project
  .in(file("."))
  .aggregate(core, coreIt, play, playIt)
  .settings(publishArtifact := false)

// Command Aliases ------------------------------

addCommandAlias("ci", ";clean ;coverage ;compile ;test ;coverageReport")
addCommandAlias("release", ";+publishSigned ;sonatypeReleaseAll")
