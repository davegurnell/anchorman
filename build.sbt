name         := "anchorman"
organization := "com.davegurnell"
version      := "0.2.1-SNAPSHOT"

scalaOrganization  in ThisBuild := "org.typelevel"
scalaVersion       in ThisBuild := "2.12.1"
crossScalaVersions in ThisBuild := Seq("2.11.8", "2.12.1")

licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0"))

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.davegurnell"        %% "unindent"          % "1.1.0",
  "com.typesafe.play"      %% "play-ws"           % "2.6.0-2017-02-12-29efe87-SNAPSHOT",
  "joda-time"               % "joda-time"         % "2.8.1",
  "org.apache.poi"          % "poi"               % "3.14",
  "org.apache.poi"          % "poi-ooxml"         % "3.14",
  "org.apache.poi"          % "poi-ooxml-schemas" % "3.14",
  "org.apache.xmlgraphics"  % "fop"               % "2.1",
  "org.scala-lang.modules" %% "scala-xml"         % "1.0.6",
  "org.scalatest"          %% "scalatest"         % "3.0.1" % Test,
  "org.scalatest"          %% "scalatest"         % "3.0.1" % IntegrationTest,
  "org.typelevel"          %% "cats"              % "0.9.0"
)

configs(IntegrationTest)

Defaults.itSettings

sonatypeProfileName := "com.davegurnell"

pomExtra in Global := {
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
