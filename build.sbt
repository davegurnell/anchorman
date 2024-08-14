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
  "org.scalatest" %% "scalatest" % "3.1.0" % IntegrationTest
)

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("releases") ++ Resolver.sonatypeOssRepos("snapshots")

// Versioning -----------------------------------

ThisBuild / versionScheme := Some("early-semver")

git.gitUncommittedChanges := git.gitCurrentTags.value.isEmpty // Put "-SNAPSHOT" on a commit if it's not a tag

// Github Actions -------------------------------

ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.11")

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

usePgpKeyHex("DF704C4F70202105")

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

lazy val anchormanCore = project
  .in(file("core"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
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

lazy val anchormanPlay = project
  .in(file("play"))
  .dependsOn(anchormanCore)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
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

lazy val anchorman = project
  .in(file("."))
  .aggregate(anchormanCore, anchormanPlay)
  .settings(publishArtifact := false)

// Command Aliases ------------------------------

addCommandAlias("ci", ";clean ;coverage ;compile ;test ;coverageReport")
addCommandAlias("release", ";+publishSigned ;sonatypeReleaseAll")
