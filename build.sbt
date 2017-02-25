organization        in ThisBuild := "com.davegurnell"
version             in ThisBuild := "0.4.0-SNAPSHOT"

scalaOrganization   in ThisBuild := "org.typelevel"
scalaVersion        in ThisBuild := "2.12.1"
crossScalaVersions  in ThisBuild := Seq("2.11.8", "2.12.1")

licenses            in Global += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0"))
sonatypeProfileName in Global := "com.davegurnell"
pomExtra            in Global := {
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

val commonScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

val commonLibraryDependencies = Seq(
  "org.scalatest"          %% "scalatest"         % "3.0.1" % Test,
  "org.scalatest"          %% "scalatest"         % "3.0.1" % IntegrationTest
)

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val core = project.in(file("core"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    name := "anchorman-core",
    libraryDependencies ++= Seq(
      "com.davegurnell"        %% "unindent"          % "1.1.0",
      "com.typesafe.play"      %% "play-ws"           % "2.6.0-M1",
      "joda-time"               % "joda-time"         % "2.8.1",
      "org.apache.poi"          % "poi"               % "3.14",
      "org.apache.poi"          % "poi-ooxml"         % "3.14",
      "org.apache.poi"          % "poi-ooxml-schemas" % "3.14",
      "org.apache.xmlgraphics"  % "fop"               % "2.1",
      "org.scala-lang.modules" %% "scala-xml"         % "1.0.6",
      "org.typelevel"          %% "cats"              % "0.9.0"
    ),
    libraryDependencies ++= commonLibraryDependencies,
    scalacOptions ++= commonScalacOptions
  )

lazy val play = project.in(file("play"))
  .dependsOn(core)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
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
