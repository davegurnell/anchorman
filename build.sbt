name         := "anchorman"
organization := "com.davegurnell"
version      := "0.2.1-SNAPSHOT"
scalaVersion := "2.11.8"

licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0"))

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "com.davegurnell"        %% "bulletin"          % "0.6.0",
  "com.davegurnell"        %% "unindent"          % "1.0.0",
  "com.typesafe.play"      %% "play-ws"           % "2.4.6",
  "joda-time"               % "joda-time"         % "2.8.1",
  "org.apache.poi"          % "poi"               % "3.14",
  "org.apache.poi"          % "poi-ooxml"         % "3.14",
  "org.apache.poi"          % "poi-ooxml-schemas" % "3.14",
  "org.apache.xmlgraphics"  % "fop"               % "2.1",
  "org.scala-lang.modules" %% "scala-xml"         % "1.0.4",
  "org.scalatest"          %% "scalatest"         % "2.2.4" % Test,
  "org.scalatest"          %% "scalatest"         % "2.2.4" % IntegrationTest,
  "org.typelevel"          %% "cats"              % "0.6.0"
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
