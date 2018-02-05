organization        in ThisBuild := "com.davegurnell"
version             in ThisBuild := "0.4.0"

scalaVersion        in ThisBuild := "2.12.4"
crossScalaVersions  in ThisBuild := Seq("2.11.11", "2.12.4")

val commonScalacOptions =
  Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-Xfatal-warnings",
    "-Ypartial-unification"
  )

val commonLibraryDependencies =
  Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "org.scalatest" %% "scalatest" % "3.0.1" % IntegrationTest
  )

def sonatypeSettings(libraryName: String) =
  Seq(
    name                := libraryName,
    publishTo           := sonatypePublishTo.value,
    publishMavenStyle   := true,
    licenses            += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    pomExtra            := {
                             <url>https://github.com/davegurnell/anchorman</url>
                             <scm>
                               <connection>scm:git:github.com/davegurnell/anchorman</connection>
                               <developerConnection>scm:git:git@github.com:davegurnell/anchorman</developerConnection>
                               <url>github.com/davegurnell/anchorman</url>
                             </scm>
                             <developers>
                               <developer>
                                 <id>davegurnell</id>
                                 <name>Dave Gurnell</name>
                                 <url>http://twitter.com/davegurnell</url>
                               </developer>
                             </developers>
                           }
  )

val disableSonatypeSettings =
  Seq(
    packagedArtifacts := Map.empty,
    publishArtifact   := false,
    publishLocal      := {},
    publish           := {},
    skip in publish   := true
  )

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val core = project.in(file("core"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(sonatypeSettings("anchorman-core"))
  .settings(
    scalacOptions       ++= commonScalacOptions,
    libraryDependencies ++= commonLibraryDependencies,
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
                            )
  )

lazy val play = project.in(file("play"))
  .dependsOn(core)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(sonatypeSettings("anchorman-play"))
  .settings(
    libraryDependencies ++= commonLibraryDependencies,
    libraryDependencies ++= Seq(
                              "com.typesafe.play" %% "play-ws"     % "2.6.11",
                              "com.typesafe.akka" %% "akka-stream" % "2.4.17" % IntegrationTest,
                              "com.typesafe.play" %% "play-ahc-ws" % "2.6.11" % IntegrationTest
                            )
  )

lazy val root = project.in(file("."))
  .aggregate(core, play)
  .settings(disableSonatypeSettings)
